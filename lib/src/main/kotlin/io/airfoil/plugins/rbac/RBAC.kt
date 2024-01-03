package io.airfoil.plugins.rbac

import io.airfoil.common.extension.withLogMetadata
import io.airfoil.plugins.rbac.config.RBACConfiguration
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.database.*
import io.ktor.server.application.*
import io.ktor.util.*
import mu.KotlinLogging

private const val TAG = "RoleBasedAccessControllerPlugin"
private val log = KotlinLogging.logger(TAG)

val rbacConfigAttrKey = AttributeKey<RBACConfiguration>("RBACConfiguration")

val Application.rbacConfig: RBACConfiguration
    get() = attributes[rbacConfigAttrKey]

fun Application.rbacConfig(rbacConfig: RBACConfiguration) {
    attributes.put(rbacConfigAttrKey, rbacConfig)
}

fun Application.loadRBACConfiguration(rbacSubjects: Set<Subject> = emptySet()): RBACConfiguration =
    RBACConfiguration.load(environment.config).also {
        if (rbacSubjects.size > 0) {
            it.subjects = rbacSubjects
        }
    }.also {
        rbacConfig(it)
    }

private val rbacControllerAttrKey = AttributeKey<RoleBasedAccessController>("RoleBasedAccessController")

val Application.rbacController: RoleBasedAccessController
    get() = attributes[rbacControllerAttrKey]

fun Application.rbacController(rbacController: RoleBasedAccessController) {
    attributes.put(rbacControllerAttrKey, rbacController)
}

fun Application.configureRBAC(
    rbacSubjects: Set<Subject> = emptySet(),
    dbUrl: String,
    dbUsername: String,
    dbPassword: String,
) {
    loadRBACConfiguration(rbacSubjects)
    configureRBACFlywayMigration(dbUrl, dbUsername, dbPassword)
    configureRBACRepositories()

    install(RoleBasedAccessControllerPlugin) {
        classPath = rbacConfig.classPath
        subjects = rbacConfig.subjects
    }
}

private val RoleBasedAccessControllerPlugin = createApplicationPlugin(
    name = "Role Based Access Controller Plugin",
    createConfiguration = ::RBACConfiguration,
) {
    log.info {
        "Configuring RBAC controller".withLogMetadata(
            "classPath" to pluginConfig.classPath,
            "subjects" to pluginConfig.subjects,
        )
    }

    application.rbacController(
        RoleBasedAccessController(
            permissionRepository = application.permissionRepository,
            roleGrantRepository = application.roleGrantRepository,
            roleRepository = application.roleRepository,
            actorGrantRepository = application.actorGrantRepository,
            actorRoleRepository = application.actorRoleRepository,
            rbacSubjects = pluginConfig.subjects,
            classPath = pluginConfig.classPath,
        )
    )
}
