package io.airfoil.plugins.rbac.data.exposed.repository

import io.airfoil.plugins.rbac.data.domain.PermissionRepository
import io.airfoil.plugins.rbac.data.domain.dto.Permission
import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.PermissionName
import io.airfoil.plugins.rbac.data.exposed.table.PermissionRecord
import io.airfoil.plugins.rbac.data.exposed.table.PermissionTable
import io.airfoil.plugins.rbac.exception.PermissionNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedPermissionRepository : PermissionRepository {
    override fun create(permission: Permission): Permission = transaction {
        PermissionRecord.new(permission.id.value) {
            this.product = permission.product.value
            this.subject = permission.subject
            this.operation = permission.operation
            this.resource = permission.resource?.value
            this.description = permission.description
        }.toDTO()
    }

    override fun update(id: PermissionId, block: (Permission) -> Permission): Permission = transaction {
        val record = PermissionRecord.findById(id.value)
            ?: throw PermissionNotFoundException(id)
        
        val updatedRecord = block(record.toDTO())

        record.apply {
            product = updatedRecord.product.value
            subject = updatedRecord.subject
            operation = updatedRecord.operation
            resource = updatedRecord.resource?.value
            description = updatedRecord.description
        }

        record.toDTO()
    }

    override fun upsert(permission: Permission): Permission = transaction {
        PermissionTable.find(
            product = permission.product.value,
            subject = permission.subject,
            operation = permission.operation,
            resource = permission.resource?.value,
        )?.let { perm ->
            update(PermissionId(perm.id.value)) {
                it.copy(
                    product = permission.product,
                    subject = permission.subject,
                    operation = permission.operation,
                    resource = permission.resource,
                    description = permission.description,
                )
            }
        } ?: create (permission)
    }

    override fun fetchOrNull(id: PermissionId): Permission? = transaction {
        PermissionRecord.findById(id.value)?.toDTO()
    }

    override fun fetchByNameOrNull(permission: PermissionName): Permission? = transaction {
        PermissionTable.find(
            product = permission.product(),
            subject = permission.subject(),
            operation = permission.operation(),
            resource = permission.resource(),
        )?.toDTO()
    }
}
