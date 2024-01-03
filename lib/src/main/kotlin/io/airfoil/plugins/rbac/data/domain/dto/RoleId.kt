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
@Serializable(with = RoleIdSerializer::class)
value class RoleId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = RoleId(UUID.randomUUID())

        operator fun invoke(string: String): RoleId? = runCatching {
            RoleId(UUID.fromString(string))
        }.getOrNull()
    }

}

object RoleIdSerializer : KSerializer<RoleId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.rbac.data.domain.dto.RoleIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): RoleId =
        RoleId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: RoleId) {
        encoder.encodeString(value.value.toString())
    }
}
