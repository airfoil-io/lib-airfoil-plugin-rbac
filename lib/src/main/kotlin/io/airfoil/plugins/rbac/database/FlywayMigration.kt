package io.airfoil.plugins.rbac.database

import io.ktor.server.application.*
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.ClassicConfiguration

private const val TAG = "RBACFlywayMigration"
private val log = KotlinLogging.logger(TAG)

private const val RBAC_DATA_MIGRATION_LOCATION = "classpath:db/rbac/migration"

fun Application.configureRBACFlywayMigration(
    dbUrl: String,
    dbUsername: String,
    dbPassword: String,
) {
    log.info("Performing RBAC flyway database migration")

    val flywayConfig = Flyway
        .configure()
        .dataSource(dbUrl, dbUsername, dbPassword)
        .table("_flyway_rbac")
        .load()
        .getConfiguration() as ClassicConfiguration
    flywayConfig.setLocationsAsStrings(*listOf(RBAC_DATA_MIGRATION_LOCATION).toTypedArray())

    Flyway(flywayConfig).also {
        it.baseline()
        it.migrate()
    }
}
