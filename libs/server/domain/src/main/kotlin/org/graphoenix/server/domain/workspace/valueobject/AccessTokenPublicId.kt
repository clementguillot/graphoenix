package org.graphoenix.server.domain.workspace.valueobject

import java.util.*

@JvmInline
value class AccessTokenPublicId(
  val value: String = UUID.randomUUID().toString(),
)
