package org.graphoenix.server.domain.metric.command

import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

data class CreateMetricCommand(
  val workspaceId: WorkspaceId,
  val durationMs: Int,
  val success: Boolean,
  val statusCode: Int,
  val entryType: String,
  val payloadSize: Long?,
)
