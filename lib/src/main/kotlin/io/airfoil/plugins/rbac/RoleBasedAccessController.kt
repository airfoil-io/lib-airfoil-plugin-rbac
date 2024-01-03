package io.airfoil.plugins.rbac

import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.common.extension.isUpperCase
import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.util.requireOrThrow
import io.airfoil.plugins.rbac.annotation.AccessControl
import io.airfoil.plugins.rbac.annotation.Permissions
import io.airfoil.plugins.rbac.data.domain.*
import io.airfoil.plugins.rbac.data.domain.dto.*
import io.airfoil.plugins.rbac.exception.AccessForbiddenException
import io.airfoil.plugins.rbac.exception.UnexpectedAccessStateException
import io.airfoil.plugins.rbac.extension.actorPermissionGrants
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import io.github.classgraph.ScanResult
import io.ktor.server.application.*
import io.ktor.server.auth.*
import mu.KotlinLogging

private const val TAG = "RoleBasedAccessController"
private val log = KotlinLogging.logger(TAG)

typealias PrincipalActorExtractor = (Principal) -> Actor

class RoleBasedAccessController(
    private val permissionRepository: PermissionRepository,
    private val roleGrantRepository: RoleGrantRepository,
    private val roleRepository: RoleRepository,
    private val actorGrantRepository: ActorGrantRepository,
    private val actorRoleRepository: ActorRoleRepository,
    private val rbacSubjects: Set<Subject>,
    classPath: String,
) {
    data class AccessRestriction(
        val permissions: List<PermissionName>,
        val require: AccessControl.Require,
    )
    
    val allPermissions: List<Permission>
    val allAccessRestrictions: Map<String, AccessRestriction>

    private fun ActorGrant.toPermissionGrant() = PermissionGrant(
        permission = allPermissions.find { it.id == permissionId }!!.toName(),
        subject = subject,
        subjectId = subjectId,
    )

    private fun Pair<Role, List<RoleGrant>>.toPermissionGrants() = buildList {
        second.map { roleGrant ->
            PermissionGrant(
                permission = allPermissions.find { it.id == roleGrant.permissionId }!!.toName(),
                subject = first.subject,
                subjectId = first.subjectId,
            )
        }.also { addAll(it) }
    }

    init {
        if (rbacSubjects.size <= 0) {
            throw InvalidConfigurationException("RBAC requires at least one subject")
        }
        rbacSubjects.forEach {
            if (!it.isUpperCase()) {
                throw InvalidConfigurationException("RBAC subjects must be uppercase")
            }
        }
        allPermissions = enumeratePermissionDefs(classPath)
        // TODO: when new permissions are enumerated and created, we need to ensure that all admin roles for the subject are granted the new permission
        allAccessRestrictions = enumerateAccessRestrictions(classPath)
    }

    fun createRole(role: Role): Role =
        roleRepository.create(role)

    fun getRole(roleId: RoleId): Role =
        roleRepository.fetch(roleId)

    fun getRoles(subject: Subject, subjectId: SubjectId): List<Role> =
        roleRepository.fetchBySubject(subject, subjectId)
    
    fun getRoleForSubjectByName(subject: Subject, subjectId: SubjectId, name: String): Role =
        roleRepository.fetchBySubjectAndName(subject, subjectId, name)

    fun getPermissionsForRole(role: Role): List<Permission> =
        getPermissionsForRole(role.id)

    fun getPermissionsForRole(roleId: RoleId): List<Permission> =
        roleGrantRepository.fetchByRole(roleId).map {
            permissionRepository.fetch(it.permissionId)
        }

    fun grantPermissionToRole(role: Role, permission: PermissionName): RoleGrant =
        grantPermissionToRole(role.id, permission)

    fun grantPermissionToRole(roleId: RoleId, permission: PermissionName): RoleGrant =
        permissionRepository.fetchByName(permission).let {
            grantPermissionToRole(roleId, it.id)
        }

    fun grantPermissionToRole(role: Role, permission: Permission): RoleGrant =
        grantPermissionToRole(role.id, permission.id)

    fun grantPermissionToRole(roleId: RoleId, permission: Permission): RoleGrant =
        grantPermissionToRole(roleId, permission.id)

    fun grantPermissionToRole(role: Role, permissionId: PermissionId): RoleGrant =
        grantPermissionToRole(role.id, permissionId)

    fun grantPermissionToRole(roleId: RoleId, permissionId: PermissionId): RoleGrant =
        roleGrantRepository.create(
            RoleGrant(
                roleId = roleId,
                permissionId = permissionId,
            )
        )

    fun revokePermissionFromRole(role: Role, permission: PermissionName) =
        revokePermissionFromRole(role.id, permission)

    fun revokePermissionFromRole(roleId: RoleId, permission: PermissionName) =
        permissionRepository.fetchByName(permission).let {
            revokePermissionFromRole(roleId, it.id)
        }

    fun revokePermissionFromRole(role: Role, permission: Permission) =
        revokePermissionFromRole(role.id, permission.id)

    fun revokePermissionFromRole(roleId: RoleId, permission: Permission) =
        revokePermissionFromRole(roleId, permission.id)

    fun revokePermissionFromRole(role: Role, permissionId: PermissionId) =
        revokePermissionFromRole(role.id, permissionId)

    fun revokePermissionFromRole(roleId: RoleId, permissionId: PermissionId) =
        roleGrantRepository.delete(
            RoleGrant(
                roleId = roleId,
                permissionId = permissionId,
            )
        )

    fun getActorsForRole(role: Role): List<ActorRole> =
        getActorsForRole(role.id)

    fun getActorsForRole(roleId: RoleId): List<ActorRole> =
        actorRoleRepository.fetchByRole(roleId)

    fun addActorToRole(role: Role, actor: Actor): ActorRole =
        addActorToRole(role.id, actor.id)

    fun addActorToRole(roleId: RoleId, actor: Actor): ActorRole =
        addActorToRole(roleId, actor.id)

    fun addActorToRole(role: Role, actorId: ActorId): ActorRole =
        addActorToRole(role.id, actorId)

    fun addActorToRole(roleId: RoleId, actorId: ActorId): ActorRole =
        actorRoleRepository.create(
            ActorRole(
                actorId = actorId,
                roleId = roleId,
            )
        )

    fun removeActorFromRole(role: Role, actor: Actor) =
        removeActorFromRole(role.id, actor.id)

    fun removeActorFromRole(roleId: RoleId, actor: Actor) =
        removeActorFromRole(roleId, actor.id)

    fun removeActorFromRole(role: Role, actorId: ActorId) =
        removeActorFromRole(role.id, actorId)

    fun removeActorFromRole(roleId: RoleId, actorId: ActorId) =
        actorRoleRepository.delete(
            ActorRole(
                actorId = actorId,
                roleId = roleId,
            )
        )

    fun grantsForActor(actor: Actor): List<PermissionGrant> = buildList {
        // TODO: add all self grants (enumerated from annotations on startup)

        // add all grants from roles the actor has
        addAll(
            actorRoleRepository.fetchByActor(actor.id).map { actorRole ->
                roleRepository.fetch(actorRole.roleId)
            }.map { role ->
                Pair(role, roleGrantRepository.fetchByRole(role.id)).toPermissionGrants()
            }.flatten(),
        )

        // add all explicit actor grants
        addAll(
            actorGrantRepository.fetchByActor(actor.id).map { actorRole ->
                actorRole.toPermissionGrant()
            },
        )
    }.distinct()

    fun onAuthenticate(call: ApplicationCall, actorExtractor: PrincipalActorExtractor) {
        call.principal<Principal>()?.also { principal ->
            val actor = actorExtractor(principal)
            grantsForActor(actor).also { grants ->
                call.actorPermissionGrants(grants)
            }
        }
    }

    fun <T> accessCheck(call: ApplicationCall, clazz: Class<T>, extractor: (Subject) -> SubjectId?): Boolean {
        return call.actorPermissionGrants.let { actorPermissions ->
            val subjects: Map<Subject, SubjectId?> = buildMap {
                rbacSubjects.forEach { put(it, extractor(it)) }
            }
            val accessRestriction = allAccessRestrictions[clazz.typeName]
            requireOrThrow(accessRestriction != null) { UnexpectedAccessStateException() }
            val accessRestrictions = accessRestriction!!.permissions.map { permission ->
                val subject = permission.subject()
                PermissionGrant(
                    permission = permission,
                    subject = subject,
                    subjectId = subjects[subject] ?: throw UnexpectedAccessStateException()
                )
            }

            when (accessRestriction.require) {
                AccessControl.Require.ALL_PERMISSIONS -> {
                    // the calling actor must have all permissions
                    (accessRestrictions subtract actorPermissions).let { missingPermissions ->
                        when (missingPermissions.size) {
                            0 -> true
                            else -> {
                                throw AccessForbiddenException(
                                    message = "Access forbidden: requires [${missingPermissions.map { it.permission }.joinToString()}]",
                                    displayMessage = "Access forbidden",
                                )
                            }
                        }
                    }
                }
                AccessControl.Require.ONE_PERMISSION -> {
                    // the calling actor must have one or more permissions
                    (accessRestrictions subtract actorPermissions).let { missingPermissions ->
                        if ((accessRestrictions.size - missingPermissions.size) > 0) {
                            true
                        } else {
                            throw AccessForbiddenException(
                                message = "Access forbidden: requires one of [${accessRestrictions.map { it.permission }.joinToString()}]",
                                displayMessage = "Access forbidden",
                            )
                        }
                    }
                }
            }
            
        }
    }

    private fun enumeratePermissionDefs(classPath: String): List<Permission> = buildList {
        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(classPath)
            .scan()
            .also { scanResult ->
                scanResult.getClassesWithAnnotation(Permissions::class.java.name).loadClasses().forEach { permClass ->
                    val permissions: Permissions = permClass.getAnnotation(Permissions::class.java)
                    permissions.groups.forEach { permGroup ->
                        permGroup.permissions.forEach { perm ->
                            val permission = Permission(
                                product = Product(permGroup.product),
                                subject = permGroup.subject,
                                operation = perm.operation,
                                resource = when (perm.resource.isBlank()) {
                                    true -> null
                                    else -> Resource(perm.resource)
                                },
                                description = perm.description,
                            )

                            add(permissionRepository.upsert(permission))

                            log.info {
                                "Discovered permission definition".withLogMetadata(
                                    "product" to permission.product,
                                    "subject" to permission.subject,
                                    "operation" to permission.operation,
                                    "resource" to permission.resource,
                                    "description" to permission.description,
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun enumerateAccessRestrictions(classPath: String): Map<String, AccessRestriction> = buildMap {
        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(classPath)
            .scan()
            .also { scanResult ->
                scanResult.getClassesWithAnnotation(AccessControl::class.java.name).loadClasses().forEach { acClass ->
                    val accessControl: AccessControl = acClass.getAnnotation(AccessControl::class.java)

                    put(acClass.typeName, AccessRestriction(
                        permissions = accessControl.permissions.map { PermissionName(it) },
                        require = accessControl.require,
                    ))

                    log.info {
                        "Discovered access restriction".withLogMetadata(
                            "class" to acClass.typeName,
                            "permissions" to accessControl.permissions,
                            "require" to accessControl.require,
                        )
                    }
                }
            }
    }
}
