package org.graphoenix.server.domain.run.valueobject

import java.util.*

@JvmInline
value class ArtifactId(
  val value: String = UUID.randomUUID().toString(),
)
