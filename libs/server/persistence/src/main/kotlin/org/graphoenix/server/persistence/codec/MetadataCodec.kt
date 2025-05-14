package org.graphoenix.server.persistence.codec

import org.bson.*
import org.bson.codecs.*
import org.graphoenix.server.persistence.entity.MetadataEntity

class MetadataCodec : Codec<MetadataEntity> {
  override fun encode(
    writer: BsonWriter,
    value: MetadataEntity,
    encoderContext: EncoderContext,
  ) = writer.run {
    writeStartDocument()
    writeFields(value)
    writeEndDocument()
  }

  private fun BsonWriter.writeFields(value: MetadataEntity) {
    writeNullableField("description", value.description) { writeString(it) }
    writeNullableField("technologies", value.technologies) { technologies ->
      writeArrayField { technologies.forEach { writeString(it) } }
    }
    writeNullableField("targetGroups", value.targetGroups) { groups ->
      writeStartDocument()
      groups.forEach { (group, targets) ->
        writeName(group)
        writeArrayField { targets.forEach { writeString(it) } }
      }
      writeEndDocument()
    }
  }

  override fun decode(
    reader: BsonReader,
    decoderContext: DecoderContext,
  ): MetadataEntity =
    reader.run {
      readStartDocument()
      val metadata = readMetadataFields()
      readEndDocument()
      metadata
    }

  private fun BsonReader.readMetadataFields(): MetadataEntity {
    val description = readNullableField("description") { readString() }
    val technologies = readNullableField("technologies") { readStringArray() }
    val targetGroups = readNullableField("targetGroups") { readTargetGroups() }
    return MetadataEntity(
      description,
      technologies,
      targetGroups,
    )
  }

  private fun BsonReader.readTargetGroups(): Map<String, Collection<String>> =
    buildMap {
      readStartDocument()
      while (readBsonType() != BsonType.END_OF_DOCUMENT) {
        val groupName = readName()
        put(groupName, readStringArray())
      }
      readEndDocument()
    }

  override fun getEncoderClass(): Class<MetadataEntity> = MetadataEntity::class.java
}
