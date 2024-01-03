package io.airfoil.plugins.rbac.data.domain

import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.domain.dto.ActorGrant

interface ActorGrantRepository {
    // create a new actor grant
    fun create(actorGrant: ActorGrant): ActorGrant

    // delete a actor grant
    fun delete(actorGrant: ActorGrant): ActorGrant

    // delete all actor grants by actor
    fun deleteAllByActor(id: ActorId) =
        fetchByActor(id).forEach { delete(it) }

    // fetch actor grants by actor
    fun fetchByActor(id: ActorId): List<ActorGrant>
}
