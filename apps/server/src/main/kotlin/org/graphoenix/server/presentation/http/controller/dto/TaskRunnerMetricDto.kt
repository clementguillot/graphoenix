package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@RegisterForReflection
data class TaskRunnerMetricDto(
  val entries: Collection<PerformanceEntry>,
) {
  data class PerformanceEntry(
    val durationMs: Int,
    val success: Boolean,
    val statusCode: Int,
    val entryType: String,
    val payloadSize: Long?,
  )

  fun toRequest(workspaceId: WorkspaceId): Collection<CreateMetricCommand> =
    entries.map {
      CreateMetricCommand(
        workspaceId = workspaceId,
        durationMs = it.durationMs,
        success = it.success,
        statusCode = it.statusCode,
        entryType = it.entryType,
        payloadSize = it.payloadSize,
      )
    }
}
