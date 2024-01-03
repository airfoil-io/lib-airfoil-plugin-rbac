package io.airfoil.plugins.rbac.data.exposed.table

import io.airfoil.plugins.rbac.data.domain.dto.ActorGrant
import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.data.domain.dto.SubjectId
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object ActorGrantTable : Table(name = "actor_grant") {
    val actorId = uuid("actor_id")
    val permissionId = reference("permission_id", PermissionTable)
    val subject = varchar("subject", 16)
    val subjectId = uuid("subject_id")

    fun new(actorId: UUID, permissionId: UUID, subject: Subject, subjectId: UUID): ActorGrantRecord =
        ActorGrantTable.insert {
            it[this.actorId] = actorId
            it[this.permissionId] = permissionId
            it[this.subject] = subject
            it[this.subjectId] = subjectId
        }.let {
            ActorGrantRecord(
                actorId = actorId,
                permissionId = permissionId,
                subject = subject,
                subjectId = subjectId,
            )
        }

    fun delete(actorId: UUID, permissionId: UUID, subject: Subject, subjectId: UUID): ActorGrantRecord =
        ActorGrantTable.deleteWhere {
            (ActorGrantTable.actorId eq actorId) and 
            (ActorGrantTable.permissionId eq permissionId) and
            (ActorGrantTable.subject eq subject) and
            (ActorGrantTable.subjectId eq subjectId)
        }.let {
            ActorGrantRecord(
                actorId = actorId,
                permissionId = permissionId,
                subject = subject,
                subjectId = subjectId,
            )
        }

    fun findByActorId(actorId: UUID): List<ActorGrantRecord> =
        ActorGrantTable.select { ActorGrantTable.actorId eq actorId }
            .withDistinct()
            .map {
                ActorGrantRecord(
                    actorId = it[ActorGrantTable.actorId],
                    permissionId = it[ActorGrantTable.permissionId].value,
                    subject = it[ActorGrantTable.subject],
                    subjectId = it[ActorGrantTable.subjectId],
                )
            }
}

class ActorGrantRecord(
    val actorId: UUID,
    val permissionId: UUID,
    val subject: Subject,
    val subjectId: UUID,
) {
    fun toDTO(): ActorGrant = ActorGrant(
        actorId = ActorId(actorId),
        permissionId = PermissionId(permissionId),
        subject = subject,
        subjectId = SubjectId(subjectId),
    )
}
