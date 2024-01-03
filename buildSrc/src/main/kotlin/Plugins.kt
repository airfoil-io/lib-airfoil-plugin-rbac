object Plugins {
    val Flyway = PluginSpec("org.flywaydb.flyway", Versions.Flyway)
    val Kotlin = PluginSpec("kotlin", Versions.Kotlin)
    object Kotlinx {
        val Serialization = PluginSpec("kotlinx-serialization", Versions.Kotlin)
    }
    val Idea = PluginSpec("idea")
}
