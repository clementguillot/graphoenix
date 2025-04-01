package org.graphoenix.server.persistence.mapper

import org.bson.types.ObjectId
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.metric.entity.TaskRunnerMetric
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.TaskRunnerMetricEntity
import java.time.LocalDateTime

fun TaskRunnerMetricEntity.toDomain(): TaskRunnerMetric =
  TaskRunnerMetric {
    workspaceId = WorkspaceId(this@toDomain.workspaceId.toString())
    recordingDate = this@toDomain.recordingDate
    durationMs = this@toDomain.durationMs
    success = this@toDomain.success
    statusCode = this@toDomain.statusCode
    entryType = this@toDomain.entryType
    payloadSize = this@toDomain.payloadSize
  }

fun CreateMetricCommand.toEntity(recordingDate: LocalDateTime): TaskRunnerMetricEntity =
  TaskRunnerMetricEntity(
    id = null,
    workspaceId = ObjectId(workspaceId.value),
    recordingDate = recordingDate,
    durationMs = durationMs,
    success = success,
    statusCode = statusCode,
    entryType = entryType,
    payloadSize = payloadSize,
  )
