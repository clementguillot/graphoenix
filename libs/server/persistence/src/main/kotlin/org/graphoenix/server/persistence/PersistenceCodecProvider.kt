package org.graphoenix.server.persistence

import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import org.graphoenix.server.persistence.codec.ProjectCodec
import org.graphoenix.server.persistence.codec.ProjectConfigurationCodec
import org.graphoenix.server.persistence.codec.ProjectGraphCodec
import org.graphoenix.server.persistence.codec.ProjectMetadataCodec
import org.graphoenix.server.persistence.entity.RunEntity

@Suppress("unused")
class PersistenceCodecProvider : CodecProvider {
  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> get(
    clazz: Class<T>,
    registry: CodecRegistry,
  ): Codec<T>? =
    when (clazz) {
      RunEntity.ProjectGraph::class.java -> ProjectGraphCodec(registry) as Codec<T>
      RunEntity.ProjectGraph.Project::class.java -> ProjectCodec(registry) as Codec<T>
      RunEntity.ProjectGraph.Project.ProjectConfiguration::class.java -> ProjectConfigurationCodec(registry) as Codec<T>
      RunEntity.ProjectGraph.Project.ProjectConfiguration.Metadata::class.java -> ProjectMetadataCodec() as Codec<T>
      else -> null
    }
}
