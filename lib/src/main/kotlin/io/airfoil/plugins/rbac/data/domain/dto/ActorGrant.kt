package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActorGrant(
    val actorId: ActorId,
    val permissionId: PermissionId,
    val subject: Subject,
    val subjectId: SubjectId,
)
