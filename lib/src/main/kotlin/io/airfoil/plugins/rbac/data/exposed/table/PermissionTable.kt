package io.airfoil.plugins.rbac.data.exposed.table

import io.airfoil.plugins.rbac.data.domain.dto.Operation
import io.airfoil.plugins.rbac.data.domain.dto.Permission
import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.Product
import io.airfoil.plugins.rbac.data.domain.dto.Resource
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.util.UUID

object PermissionTable : UUIDTable(
    name = "permission",
    columnName = "id",
) {
    val product = text("product")
    val subject = varchar("subject", 16)
    val operation = enumerationByName("operation", 8, Operation::class)
    val resource = text("resource").nullable()
    val description = text("description")

    fun entityOf(id: UUID) = EntityID<UUID>(id, PermissionTable)

    fun find(product: String, subject: Subject, operation: Operation, resource: String?): PermissionRecord? =
        select {
            (PermissionTable.product eq product) and 
            (PermissionTable.subject eq subject) and 
            (PermissionTable.operation eq operation) and 
            (PermissionTable.resource eq resource)
        }
            .toList()
            .firstOrNull()?.let { PermissionRecord.findById(it[id].value) }
}

class PermissionRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionRecord>(PermissionTable)

    var product by PermissionTable.product
    var subject by PermissionTable.subject
    var operation by PermissionTable.operation
    var resource by PermissionTable.resource
    var description by PermissionTable.description

    fun toDTO(): Permission = Permission(
        id = PermissionId(id.value),
        product = Product(product),
        subject = subject,
        operation = operation,
        resource = resource?.let { Resource(it) },
        description = description,
    )
}
