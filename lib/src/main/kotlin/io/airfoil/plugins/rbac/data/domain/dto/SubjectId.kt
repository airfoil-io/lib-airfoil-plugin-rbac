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
@Serializable(with = SubjectIdSerializer::class)
value class SubjectId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = SubjectId(UUID.randomUUID())

        operator fun invoke(string: String): SubjectId? = runCatching {
            SubjectId(UUID.fromString(string))
        }.getOrNull()
    }

}

object SubjectIdSerializer : KSerializer<SubjectId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.rbac.data.domain.dto.SubjectIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): SubjectId =
        SubjectId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: SubjectId) {
        encoder.encodeString(value.value.toString())
    }
}
