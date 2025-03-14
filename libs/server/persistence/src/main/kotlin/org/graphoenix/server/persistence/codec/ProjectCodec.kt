package org.graphoenix.server.persistence.codec

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistry
import org.graphoenix.server.persistence.entity.RunEntity

class ProjectCodec(
  private val registry: CodecRegistry,
) : Codec<RunEntity.ProjectGraph.Project> {
  private val configurationCodec by lazy {
    registry[RunEntity.ProjectGraph.Project.ProjectConfiguration::class.java]
  }

  override fun encode(
    writer: BsonWriter,
    value: RunEntity.ProjectGraph.Project,
    encoderContext: EncoderContext,
  ) = writer.run {
    writeStartDocument()
    writeFields(value, encoderContext)
    writeEndDocument()
  }

  private fun BsonWriter.writeFields(
    value: RunEntity.ProjectGraph.Project,
    encoderContext: EncoderContext,
  ) {
    writeString("type", value.type)
    writeString("name", value.name)
    writeRequiredField("data") {
      configurationCodec.encode(this, value.data, encoderContext)
    }
  }

  override fun decode(
    reader: BsonReader,
    decoderContext: DecoderContext,
  ): RunEntity.ProjectGraph.Project =
    reader.run {
      readStartDocument()
      val project = readProjectFields(decoderContext)
      readEndDocument()
      project
    }

  private fun BsonReader.readProjectFields(decoderContext: DecoderContext): RunEntity.ProjectGraph.Project {
    val type = readRequiredField("type") { readString() }
    val name = readRequiredField("name") { readString() }
    val data =
      readRequiredField("data") {
        configurationCodec.decode(this, decoderContext)
      }

    return RunEntity.ProjectGraph.Project(
      type = type,
      name = name,
      data = data,
    )
  }

  override fun getEncoderClass(): Class<RunEntity.ProjectGraph.Project> = RunEntity.ProjectGraph.Project::class.java
}
