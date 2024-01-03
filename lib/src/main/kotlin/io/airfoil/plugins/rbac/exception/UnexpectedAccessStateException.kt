package io.airfoil.plugins.rbac.exception

import io.airfoil.common.exception.UnexpectedStateException

class UnexpectedAccessStateException : UnexpectedStateException(
    message = "Unexpected access state in restricted usecase",
    displayMessage = "Unexpected application state",
    category = ErrorCategories.RBAC,
)
