package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class Actor(
    val id: ActorId,
    val type: ActorType,
)
