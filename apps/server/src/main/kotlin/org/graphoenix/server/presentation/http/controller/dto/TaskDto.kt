package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.domain.run.entity.Task
import java.time.LocalDateTime

@RegisterForReflection
data class TaskDto(
  val taskId: String,
  val projectName: String,
  val target: String,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val cacheStatus: String,
  val params: String,
  val status: Int,
) {
  companion object {
    fun from(task: Task) =
      TaskDto(
        taskId = task.taskId.value,
        projectName = task.projectName,
        target = task.target,
        startTime = task.startTime,
        endTime = task.endTime,
        cacheStatus = task.cacheStatus.value,
        params = task.params,
        status = task.status,
      )
  }
}
