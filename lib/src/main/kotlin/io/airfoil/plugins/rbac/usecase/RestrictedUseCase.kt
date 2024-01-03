package io.airfoil.plugins.rbac.usecase

import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.airfoil.plugins.rbac.data.domain.dto.SubjectId
import io.airfoil.plugins.rbac.exception.AccessForbiddenException
import io.ktor.server.application.*

abstract class RestrictedUseCase<Args, Res> {
    suspend fun execute(args: Args, call: ApplicationCall, allow: () -> Boolean): Res =
        when (allow()) {
            true -> run(args, call)
            false -> throw AccessForbiddenException("Access forbidden")
        }

    abstract fun extractSubject(subject: Subject, args: Args): SubjectId?
    
    protected abstract suspend fun run(args: Args, call: ApplicationCall): Res
}
