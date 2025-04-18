package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.valueobject.RunStatus
import java.time.Duration
import java.time.LocalDateTime

data class RunSimpleDto(
  val id: String,
  val linkId: String,
  val status: RunStatus,
  val command: String,
  val branch: String?,
//  val commit: String?,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val duration: String,
) {
  companion object {
    fun from(run: Run) =
      RunSimpleDto(
        id = run.id.value,
        linkId = run.linkId,
        status = run.status,
        command = run.command,
        branch = run.branch,
//        commit = run.vcsContext?.let {
//          "${it.headSha?.take(8)} - ${it.title}"
//        },
        startTime = run.startTime,
        endTime = run.endTime,
        duration =
          Duration.between(run.startTime, run.endTime).abs().let { duration ->
            val minutes = duration.toMinutes()
            val seconds = duration.seconds % 60

            if (minutes > 0) {
              "$minutes m $seconds s"
            } else {
              "$seconds s"
            }
          },
      )
  }
}
