package io.airfoil.plugins.rbac.data.exposed.repository

import io.airfoil.plugins.rbac.data.domain.RoleRepository
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.data.domain.dto.SubjectId
import io.airfoil.plugins.rbac.data.domain.dto.Role
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.plugins.rbac.data.exposed.table.RoleRecord
import io.airfoil.plugins.rbac.data.exposed.table.RoleTable
import io.airfoil.plugins.rbac.exception.RoleNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedRoleRepository : RoleRepository {
    override fun create(role: Role): Role = transaction {
        RoleRecord.new(role.id.value) {
            this.subject = role.subject
            this.subjectId = role.subjectId.value
            this.name = role.name
            this.description = role.description
            this.mutable = role.mutable
            this.admin = role.admin
        }.toDTO()
    }
    
    override fun update(id: RoleId, block: (Role) -> Role): Role = transaction {
        val record = RoleRecord.findById(id.value)
            ?: throw RoleNotFoundException(id)
        
        val updatedRecord = block(record.toDTO())

        record.apply {
            subject = updatedRecord.subject
            subjectId = updatedRecord.subjectId.value
            name = updatedRecord.name
            description = updatedRecord.description
            mutable = updatedRecord.mutable
            admin = updatedRecord.admin
        }

        record.toDTO()
    }
    
    override fun upsert(role: Role): Role = transaction {
        RoleRecord.findById(role.id.value)?.let {
            update(role.id) {
                it.copy(
                    subject = role.subject,
                    subjectId = role.subjectId,
                    name = role.name,
                    description = role.description,
                    mutable = role.mutable,
                    admin = role.admin,
                )
            }
        } ?: create (role)
    }

    override fun fetchOrNull(id: RoleId): Role? = transaction {
        RoleRecord.findById(id.value)?.toDTO()
    }

    override fun fetchBySubject(subject: Subject, subjectId: SubjectId): List<Role> = transaction {
        RoleTable.findBySubject(subject, subjectId.value).map { it.toDTO() }
    }

    override fun fetchBySubjectAndNameOrNull(subject: Subject, subjectId: SubjectId, roleName: String): Role? = transaction {
        RoleTable.findBySubjectAndName(subject, subjectId.value, roleName)?.toDTO()
    }
}
