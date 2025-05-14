package org.graphoenix.server.persistence.codec

import org.bson.*
import org.bson.codecs.*
import org.bson.codecs.configuration.CodecRegistry
import org.graphoenix.server.persistence.entity.RunEntity

class ProjectGraphCodec(
  private val registry: CodecRegistry,
) : Codec<RunEntity.ProjectGraph> {
  private val projectCodec by lazy {
    registry[RunEntity.ProjectGraph.Project::class.java]
  }

  override fun encode(
    writer: BsonWriter,
    value: RunEntity.ProjectGraph,
    encoderContext: EncoderContext,
  ) = writer.run {
    writeStartDocument()
    writeFields(value, encoderContext)
    writeEndDocument()
  }

  private fun BsonWriter.writeFields(
    value: RunEntity.ProjectGraph,
    encoderContext: EncoderContext,
  ) {
    writeRequiredField("nodes") {
      writeDocumentMap(value.nodes) { project ->
        projectCodec.encode(this, project, encoderContext)
      }
    }

    writeRequiredField("dependencies") {
      writeDocumentMap(value.dependencies) { deps ->
        writeStartArray()
        deps.forEach { writeDependency(it) }
        writeEndArray()
      }
    }
  }

  private fun BsonWriter.writeDependency(dependency: RunEntity.ProjectGraph.Dependency) {
    writeStartDocument()
    writeString("source", dependency.source)
    writeString("target", dependency.target)
    writeString("type", dependency.type)
    writeEndDocument()
  }

  override fun decode(
    reader: BsonReader,
    decoderContext: DecoderContext,
  ): RunEntity.ProjectGraph =
    reader.run {
      readStartDocument()
      val graph = readGraphFields(decoderContext)
      readEndDocument()
      graph
    }

  private fun BsonReader.readGraphFields(decoderContext: DecoderContext): RunEntity.ProjectGraph {
    val nodes =
      readRequiredField("nodes") {
        readDocumentMap { projectCodec.decode(this, decoderContext) }
      }

    val dependencies =
      readRequiredField("dependencies") {
        readDocumentMap { readDependenciesList() }
      }

    return RunEntity.ProjectGraph(
      nodes = nodes,
      dependencies = dependencies,
    )
  }

  private fun BsonReader.readDependenciesList(): List<RunEntity.ProjectGraph.Dependency> =
    buildList {
      readStartArray()
      while (readBsonType() != BsonType.END_OF_DOCUMENT) {
        add(readDependency())
      }
      readEndArray()
    }

  private fun BsonReader.readDependency(): RunEntity.ProjectGraph.Dependency {
    readStartDocument()
    val dependency =
      RunEntity.ProjectGraph.Dependency(
        source = readRequiredField("source") { readString() },
        target = readRequiredField("target") { readString() },
        type = readRequiredField("type") { readString() },
      )
    readEndDocument()
    return dependency
  }

  override fun getEncoderClass(): Class<RunEntity.ProjectGraph> = RunEntity.ProjectGraph::class.java
}
