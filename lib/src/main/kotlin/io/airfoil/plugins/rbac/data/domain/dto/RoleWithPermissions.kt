package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoleWithPermissions(
    override val id: RoleId = RoleId.random(),
    override val subject: Subject,
    override val subjectId: SubjectId,
    override val name: String,
    override val description: String? = null,
    override val mutable: Boolean = true,
    override val admin: Boolean = false,
    val permissions: List<PermissionName>,
) : BaseRole {

    fun withoutPermissions() = Role(
        id = id,
        subject = subject,
        subjectId = subjectId,
        name = name,
        description = description,
        mutable = mutable,
        admin = admin,
    )
}