package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class PermissionGrant(
    val permission: PermissionName,
    val subject: Subject,
    val subjectId: SubjectId,
)
