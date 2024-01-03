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
@Serializable(with = ActorIdSerializer::class)
value class ActorId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = ActorId(UUID.randomUUID())

        operator fun invoke(string: String): ActorId? = runCatching {
            ActorId(UUID.fromString(string))
        }.getOrNull()
    }

}

object ActorIdSerializer : KSerializer<ActorId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.rbac.data.domain.dto.ActorIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ActorId =
        ActorId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: ActorId) {
        encoder.encodeString(value.value.toString())
    }
}
