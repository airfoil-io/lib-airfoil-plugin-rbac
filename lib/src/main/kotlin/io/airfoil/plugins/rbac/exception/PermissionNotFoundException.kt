package io.airfoil.plugins.rbac.exception

import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.PermissionName
import io.airfoil.common.exception.ResourceNotFoundException

class PermissionNotFoundException(id: PermissionId? = null, name: PermissionName? = null) : ResourceNotFoundException(
    message = id?.let { "Permission $id does not exist" }
        ?: name?.let { "Permission $name does not exist" } 
        ?: "Permission <unknown> does not exist",
    category = ErrorCategories.RBAC,
)
