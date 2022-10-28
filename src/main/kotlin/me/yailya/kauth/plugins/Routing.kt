/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.yailya.kauth.JWT_AUDIENCE
import me.yailya.kauth.JWT_ISSUER
import me.yailya.kauth.JWT_SECRET
import me.yailya.kauth.database.account.AccountEntity
import me.yailya.kauth.database.account.Accounts
import me.yailya.kauth.database.application.ApplicationEntity
import me.yailya.kauth.database.application.webhook.ApplicationWebhookTrigger
import me.yailya.kauth.exceptions.PrintableException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import java.util.*

@Suppress("SpellCheckingInspection")
fun Application.configureRouting() {
    val openApi = Thread.currentThread().contextClassLoader!!
        .getResourceAsStream("kauth.openapi.yaml")!!
        .reader().readText() + "\nhost: ${me.yailya.kauth.HOST}"

    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            throwable.printStackTrace()

            when {
                throwable is PrintableException -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "error" to throwable.name,
                            "message" to throwable.message
                        )
                    )
                }

                throwable is ExposedSQLException && throwable.message!!.contains("Value too long") -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "error" to "SQLException",
                            "message" to "Value too long"
                        )
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "error" to throwable.javaClass.simpleName
                        )
                    )
                }
            }
        }
    }

    routing {
        get("login") {
            val queryParameters = call.request.queryParameters
            val user = AccountEntity.getByOp {
                (Accounts.email eq queryParameters["email"]!!) and (Accounts.password eq queryParameters["password"]!!)
            }!!

            call.respond(
                HttpStatusCode.Created,
                hashMapOf(
                    "token" to JWT
                        .create()
                        .withAudience(JWT_AUDIENCE)
                        .withIssuer(JWT_ISSUER)
                        .withClaim("id", user.id.value.toString())
                        .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000))
                        .sign(Algorithm.HMAC256(JWT_SECRET))
                        .also { user.updateEntity { token = it } }
                )
            )
        }

        get("register") {
            val queryParameters = call.request.queryParameters
            val user = AccountEntity.create(
                queryParameters["username"]!!,
                queryParameters["email"]!!,
                queryParameters["password"]!!
            )

            call.respond(
                HttpStatusCode.Created,
                hashMapOf(
                    "token" to JWT
                        .create()
                        .withAudience(JWT_AUDIENCE)
                        .withIssuer(JWT_ISSUER)
                        .withClaim("id", user.id.value.toString())
                        .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000))
                        .sign(Algorithm.HMAC256(JWT_SECRET))
                        .also { user.updateEntity { token = it.toString() } }
                )
            )
        }

        get("validate") {
            val queryParameters = call.request.queryParameters

            try {
                val application = ApplicationEntity
                    .getById(UUID.fromString(queryParameters["application_id"]))
                val result = application
                    .getApplicationUserModels()
                    .any { it.key == queryParameters["key"] && it.hwid == queryParameters["hwid"] }

                application.getApplicationWebhookModels()
                    .filter { it.trigger == ApplicationWebhookTrigger.Validate }
                    .forEach {
                        it.execute(
                            application.id.toString(),
                            queryParameters["key"]!!,
                            queryParameters["hwid"]!!,
                            result.toString()
                        )
                    }

                call.respond(hashMapOf("valid" to result))
            } catch (ex: Exception) {
                call.respond(
                    hashMapOf(
                        "valid" to false
                    )
                )
            }
        }

        get("openapi") {
            call.respondText(openApi)
        }

        static("swagger") {
            defaultResource("kauth.swagger.html")
        }

        authenticate("auth-jwt") {
            get("api") {
                val queryParameters = call.request.queryParameters

                when (call.request.queryParameters["type"]) {
                    "get_applications" -> {
                        call.respond(call.validUserToken().getApplicationModels())
                    }

                    "get_application" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .toModel()
                        )
                    }

                    "create_application" -> {
                        call.respond(
                            call.validUserToken()
                                .createApplication(queryParameters["name"]!!)
                                .toModel()
                        )
                    }

                    "update_application" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .updateEntity {
                                    val name = queryParameters["name"]

                                    if (name != null) this.name = name
                                }.toModel()
                        )
                    }

                    "delete_application" -> {
                        call.validUserToken()
                            .deleteApplication(UUID.fromString(queryParameters["application_id"]))
                    }

                    "get_application_users" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationUserModels()
                        )
                    }

                    "get_application_user" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationUser(UUID.fromString(queryParameters["user_id"]))
                                .toModel()
                        )
                    }

                    "create_application_user" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .createApplicationUser(queryParameters["key"]!!, queryParameters["hwid"]!!)
                                .toModel()
                        )
                    }

                    "update_application_user" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationUser(UUID.fromString(queryParameters["user_id"]))
                                .updateEntity {
                                    if (queryParameters["key"] != null) this.key = queryParameters["key"]!!
                                    if (queryParameters["hwid"] != null) this.hwid = queryParameters["hwid"]!!
                                }.toModel()
                        )
                    }

                    "delete_application_user" -> {
                        call.validUserToken()
                            .getApplication(UUID.fromString(queryParameters["application_id"]))
                            .deleteApplicationUser(UUID.fromString(queryParameters["user_id"]))
                    }

                    "get_application_webhooks" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationWebhookModels()
                        )
                    }

                    "get_application_webhook" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationWebhook(UUID.fromString(queryParameters["webhook_id"]))
                                .toModel()
                        )
                    }

                    "create_application_webhook" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .createApplicationWebhook(queryParameters["trigger"]!!, queryParameters["url"]!!)
                                .toModel()
                        )
                    }

                    "update_application_webhook" -> {
                        call.respond(
                            call.validUserToken()
                                .getApplication(UUID.fromString(queryParameters["application_id"]))
                                .getApplicationWebhook(UUID.fromString(queryParameters["webhook_id"]))
                                .updateEntity {
                                    if (queryParameters["trigger"] != null) this.trigger = ApplicationWebhookTrigger
                                        .valueOf(queryParameters["trigger"]!!)
                                        .toString()
                                    if (queryParameters["url"] != null) this.url = queryParameters["url"]!!
                                }.toModel()
                        )
                    }

                    "delete_application_webhook" -> {
                        call.validUserToken()
                            .getApplication(UUID.fromString(queryParameters["application_id"]))
                            .deleteApplicationWebhook(UUID.fromString(queryParameters["webhook_id"]))
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.validUserToken(): AccountEntity {
    val token = this.principal<JWTPrincipal>()!!
    val tokenString = request.authorization()!!.removePrefix("Bearer ")
    val user = AccountEntity.getById(UUID.fromString(token.payload.getClaim("id").asString()))

    if (tokenString != user.token) {
        throw PrintableException("OutdatedToken", "Token is outdated")
    }

    return user
}
