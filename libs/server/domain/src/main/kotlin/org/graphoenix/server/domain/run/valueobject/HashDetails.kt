package org.graphoenix.server.domain.run.valueobject

data class HashDetails(
  val nodes: Map<String, String>,
  val runtime: Map<String, String>?,
  val implicitDeps: Map<String, String>?,
)
