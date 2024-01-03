package io.airfoil.plugins.rbac.exception

import io.airfoil.common.exception.AirfoilException

open class AccessForbiddenException(
    message: String,
    displayMessage: String = message,
) : AirfoilException(
    message = message,
    displayMessage = displayMessage,
    category = ErrorCategories.RBAC,
)
