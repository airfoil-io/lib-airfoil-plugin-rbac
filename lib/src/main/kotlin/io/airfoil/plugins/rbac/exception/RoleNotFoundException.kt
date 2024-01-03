package io.airfoil.plugins.rbac.exception

import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.common.exception.ResourceNotFoundException

class RoleNotFoundException(id: RoleId? = null, name: String? = null) : ResourceNotFoundException(
    message = if (id != null) {
        "Role $id does not exist"
    } else if (name != null) {
        "Role with name $name does not exist"
    } else {
        "Role does not exist"
    },
    category = ErrorCategories.RBAC,
)
