package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import kotlin.runCatching

@JvmInline
@Serializable(with = PermissionIdSerializer::class)
value class PermissionId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = PermissionId(UUID.randomUUID())

        operator fun invoke(string: String): PermissionId? = runCatching {
            PermissionId(UUID.fromString(string))
        }.getOrNull()
    }

}

object PermissionIdSerializer : KSerializer<PermissionId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.rbac.data.domain.dto.PermissionIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PermissionId =
        PermissionId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: PermissionId) {
        encoder.encodeString(value.value.toString())
    }
}
