package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActorRole(
    val actorId: ActorId,
    val roleId: RoleId,
)
