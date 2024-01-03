package io.airfoil.plugins.rbac.extension

import io.airfoil.plugins.rbac.rbacController
import io.airfoil.plugins.rbac.data.domain.dto.PermissionGrant
import io.airfoil.plugins.rbac.usecase.RestrictedUseCase
import io.airfoil.plugins.rbac.usecase.UnrestrictedUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.*

private val actorPermissionGrantsKey = AttributeKey<List<PermissionGrant>>("ActorPermissionGrants")

fun ApplicationCall.actorPermissionGrants(grants: List<PermissionGrant>) =
    attributes.put(actorPermissionGrantsKey, grants)

val ApplicationCall.actorPermissionGrants: List<PermissionGrant>
    get() = attributes[actorPermissionGrantsKey]

suspend fun <Args, Res> ApplicationCall.execute(usecase: RestrictedUseCase<Args, Res>, args: Args): Res =
    usecase.execute(args, this) {
        application.rbacController.accessCheck(
            call = this,
            clazz = usecase.javaClass,
        ) { sub -> usecase.extractSubject(sub, args) }
    }

suspend fun <Args, Res> ApplicationCall.executeWithStatus(
    usecase: RestrictedUseCase<Args, Res>,
    args: Args,
    status: HttpStatusCode = HttpStatusCode.OK,
): Res =
    usecase.execute(args, this) {
        application.rbacController.accessCheck(
            call = this,
            clazz = usecase.javaClass,
        ) { sub -> usecase.extractSubject(sub, args) }
    }.also {
        response.status(status)
    }

suspend inline fun <Args, reified Res: Any> ApplicationCall.executeAndRespond(usecase: RestrictedUseCase<Args, Res>, args: Args): Res =
    execute(usecase, args).also { result ->
        respond(result)
    }

suspend inline fun <Args> ApplicationCall.executeAndRespondBytes(
    usecase: RestrictedUseCase<Args, ByteArray>,
    args: Args,
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
): ByteArray =
    execute(usecase, args).also { result ->
        respondBytes(
            contentType = contentType,
            status = status,
            provider = { result },
        )
    }

suspend fun <Args, Res> ApplicationCall.execute(usecase: UnrestrictedUseCase<Args, Res>, args: Args): Res =
    usecase.execute(args, this)

suspend fun <Args, Res> ApplicationCall.executeWithStatus(
    usecase: UnrestrictedUseCase<Args, Res>,
    args: Args,
    status: HttpStatusCode = HttpStatusCode.OK,
): Res =
    usecase.execute(args, this).also {
        response.status(status)
    }

suspend inline fun <Args, reified Res: Any> ApplicationCall.executeAndRespond(usecase: UnrestrictedUseCase<Args, Res>, args: Args): Res =
    execute(usecase, args).also { result ->
        respond(result)
    }

suspend inline fun <Args> ApplicationCall.executeAndRespondBytes(
    usecase: UnrestrictedUseCase<Args, ByteArray>,
    args: Args,
    contentType: ContentType? = null,
    status: HttpStatusCode? = null,
): ByteArray =
    execute(usecase, args).also { result ->
        respondBytes(
            contentType = contentType,
            status = status,
            provider = { result },
        )
    }
