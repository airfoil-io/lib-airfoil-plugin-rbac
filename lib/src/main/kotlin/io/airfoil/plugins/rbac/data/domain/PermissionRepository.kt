package io.airfoil.plugins.rbac.data.domain

import io.airfoil.plugins.rbac.data.domain.dto.Permission
import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.PermissionName
import io.airfoil.plugins.rbac.exception.PermissionNotFoundException

interface PermissionRepository {
    // create a new permission
    fun create(permission: Permission): Permission
    
    // update an existing permission in the repository
    fun update(id: PermissionId, block: (Permission) -> Permission): Permission
    
    // upsert (create or update) a permission in the repository
    fun upsert(permission: Permission): Permission

    // fetch a permission by id
    fun fetchOrNull(id: PermissionId): Permission?
    fun fetch(id: PermissionId): Permission =
        fetchOrNull(id) ?: throw PermissionNotFoundException(id)

    // fetch a permission by name
    fun fetchByNameOrNull(permission: PermissionName): Permission?
    fun fetchByName(permission: PermissionName): Permission =
        fetchByNameOrNull(permission) ?: throw PermissionNotFoundException(name = permission)
}
