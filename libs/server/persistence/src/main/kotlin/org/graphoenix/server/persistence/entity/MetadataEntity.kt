package org.graphoenix.server.persistence.entity

import io.quarkus.mongodb.panache.common.MongoEntity

@MongoEntity
data class MetadataEntity(
  var description: String?,
  var technologies: Collection<String>?,
  var targetGroups: Map<String, Collection<String>>?,
  // missing `owners?`
)
