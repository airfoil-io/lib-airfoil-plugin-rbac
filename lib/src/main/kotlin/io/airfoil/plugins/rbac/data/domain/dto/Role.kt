package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

interface BaseRole {
    val id: RoleId
    val subject: Subject
    val subjectId: SubjectId
    val name: String
    val description: String?
    val mutable: Boolean
    val admin: Boolean
}

@Serializable
data class Role(
    override val id: RoleId = RoleId.random(),
    override val subject: Subject,
    override val subjectId: SubjectId,
    override val name: String,
    override val description: String? = null,
    override val mutable: Boolean = true,
    override val admin: Boolean = false,
) : BaseRole {
    
    fun withPermissions(permissions: List<PermissionName>) = RoleWithPermissions(
        id = id,
        subject = subject,
        subjectId = subjectId,
        name = name,
        description = description,
        mutable = mutable,
        admin = admin,
        permissions = permissions,
    )
}
