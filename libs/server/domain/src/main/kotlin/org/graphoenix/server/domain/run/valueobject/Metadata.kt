package org.graphoenix.server.domain.run.valueobject

data class Metadata(
  val description: String?,
  val technologies: Collection<String>?,
  val targetGroups: Map<String, Collection<String>>?,
  // missing `owners?`
)
