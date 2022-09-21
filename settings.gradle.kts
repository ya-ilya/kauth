pluginManagement {
    val ktorVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        application
    }
}

rootProject.name = "kauth"