package io.airfoil.plugins.rbac.data.domain

import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.domain.dto.ActorRole
import io.airfoil.plugins.rbac.data.domain.dto.RoleId

interface ActorRoleRepository {
    // create a new actor role
    fun create(actorRole: ActorRole): ActorRole

    // delete a actor role
    fun delete(actorRole: ActorRole): ActorRole

    // fetch actor roles by role
    fun fetchByRole(id: RoleId): List<ActorRole>

    // fetch actor roles by actor
    fun fetchByActor(id: ActorId): List<ActorRole>
}
