package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.domain.run.entity.Run
import java.time.LocalDateTime

@RegisterForReflection
data class RunSimpleDto(
  val id: String,
  val linkId: String,
  val status: Int,
  val command: String,
  val branch: String?,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val workspaceId: String,
) {
  companion object {
    fun from(run: Run) =
      RunSimpleDto(
        id = run.id.value,
        linkId = run.linkId.value,
        status = run.status,
        command = run.command,
        branch = run.branch,
        startTime = run.startTime,
        endTime = run.endTime,
        workspaceId = run.workspaceId.value,
      )
  }
}
