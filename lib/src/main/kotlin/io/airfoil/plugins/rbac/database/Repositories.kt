package io.airfoil.plugins.rbac.database

import io.airfoil.plugins.rbac.data.domain.ActorGrantRepository
import io.airfoil.plugins.rbac.data.domain.ActorRoleRepository
import io.airfoil.plugins.rbac.data.domain.PermissionRepository
import io.airfoil.plugins.rbac.data.domain.RoleGrantRepository
import io.airfoil.plugins.rbac.data.domain.RoleRepository
import io.airfoil.plugins.rbac.data.exposed.repository.*
import io.ktor.server.application.*
import io.ktor.util.*

fun Application.configureRBACRepositories() {
    actorGrantRepository(ExposedActorGrantRepository())
    actorRoleRepository(ExposedActorRoleRepository())
    permissionRepository(ExposedPermissionRepository())
    roleGrantRepository(ExposedRoleGrantRepository())
    roleRepository(ExposedRoleRepository())
}

private val actorGrantRepositoryAttrKey = AttributeKey<ActorGrantRepository>("ActorGrantRepository")

val Application.actorGrantRepository: ActorGrantRepository
    get() = attributes[actorGrantRepositoryAttrKey]

fun Application.actorGrantRepository(actorGrantRepository: ActorGrantRepository) {
    attributes.put(actorGrantRepositoryAttrKey, actorGrantRepository)
}

private val actorRoleRepositoryAttrKey = AttributeKey<ActorRoleRepository>("ActorRoleRepository")

val Application.actorRoleRepository: ActorRoleRepository
    get() = attributes[actorRoleRepositoryAttrKey]

fun Application.actorRoleRepository(actorRoleRepository: ActorRoleRepository) {
    attributes.put(actorRoleRepositoryAttrKey, actorRoleRepository)
}

private val permissionRepositoryAttrKey = AttributeKey<PermissionRepository>("PermissionRepository")

val Application.permissionRepository: PermissionRepository
    get() = attributes[permissionRepositoryAttrKey]

fun Application.permissionRepository(permissionRepository: PermissionRepository) {
    attributes.put(permissionRepositoryAttrKey, permissionRepository)
}

private val roleGrantRepositoryAttrKey = AttributeKey<RoleGrantRepository>("RoleGrantRepository")

val Application.roleGrantRepository: RoleGrantRepository
    get() = attributes[roleGrantRepositoryAttrKey]

fun Application.roleGrantRepository(roleGrantRepository: RoleGrantRepository) {
    attributes.put(roleGrantRepositoryAttrKey, roleGrantRepository)
}

private val roleRepositoryAttrKey = AttributeKey<RoleRepository>("RoleRepository")

val Application.roleRepository: RoleRepository
    get() = attributes[roleRepositoryAttrKey]

fun Application.roleRepository(roleRepository: RoleRepository) {
    attributes.put(roleRepositoryAttrKey, roleRepository)
}
