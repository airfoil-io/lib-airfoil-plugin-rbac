package io.airfoil.plugins.rbac.data.exposed.table

import io.airfoil.plugins.rbac.data.domain.dto.Role
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.data.domain.dto.SubjectId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import java.util.UUID

object RoleTable : UUIDTable(
    name = "role",
    columnName = "id",
) {
    val subject = varchar("subject", 16)
    val subjectId = uuid("subject_id")
    val name = text("name")
    val description = text("description").nullable()
    val mutable = bool("mutable")
    val admin = bool("admin")

    fun entityOf(id: UUID) = EntityID<UUID>(id, RoleTable)

    fun findBySubject(subject: Subject, subjectId: UUID): List<RoleRecord> =
        RoleTable.select { (RoleTable.subject eq subject) and (RoleTable.subjectId eq subjectId) }
            .withDistinct()
            .map { it.toRoleRecord() }
            .filterNotNull()

    fun findBySubjectAndName(subject: Subject, subjectId: UUID, name: String): RoleRecord? =
        RoleTable.select { (RoleTable.subject eq subject) and (RoleTable.subjectId eq subjectId) and (RoleTable.name eq name) }
            .toList().firstOrNull()?.toRoleRecord()
}

private fun ResultRow.toRoleRecord() = RoleRecord.wrapRow(this)

class RoleRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleRecord>(RoleTable)

    var subject by RoleTable.subject
    var subjectId by RoleTable.subjectId
    var name by RoleTable.name
    var description by RoleTable.description
    var mutable by RoleTable.mutable
    var admin by RoleTable.admin

    fun toDTO(): Role = Role(
        id = RoleId(id.value),
        subject = subject,
        subjectId = SubjectId(subjectId),
        name = name,
        description = description,
        mutable = mutable,
        admin = admin,
    )
}
