package io.airfoil.plugins.rbac.usecase

import io.ktor.server.application.*

abstract class UnrestrictedUseCase<Args, Res> {
    suspend fun execute(args: Args, call: ApplicationCall): Res =
        run(args, call)

    protected abstract suspend fun run(args: Args, call: ApplicationCall): Res
}
