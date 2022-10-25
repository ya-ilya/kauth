/*
 * Copyright (c) 2022. ya-ilya
 */

package me.yailya.kauth.database.application.webhook

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.yailya.kauth.serializers.UUIDSerializer
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Serializable
data class ApplicationWebhookModel(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @Serializable(UUIDSerializer::class)
    val applicationId: UUID,
    val trigger: ApplicationWebhookTrigger,
    val url: String
) {
    @Serializable
    private data class WebhookRequestModel(val content: String)

    fun execute(vararg arguments: String) {
        try {
            val url = URL(url)
            val urlConnection = url.openConnection() as HttpURLConnection

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.setRequestProperty("User-Agent", "kauth")
            urlConnection.doOutput = true
            urlConnection.outputStream.use {
                it.write(
                    Json.encodeToString(
                        WebhookRequestModel(
                            when (trigger) {
                                ApplicationWebhookTrigger.Validate -> {
                                    "Trigger: Validate. Application: ${arguments[0]}. Key: ${arguments[1]}. Hwid: ${arguments[2]}. Result: ${arguments[3]}"
                                }
                            }
                        )
                    ).toByteArray()
                )
                it.flush()
            }
            urlConnection.inputStream.close()
            urlConnection.disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}