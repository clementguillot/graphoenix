package org.graphoenix.server.gateway.persistence

import org.bson.types.ObjectId
import org.graphoenix.server.domain.metric.model.TaskRunnerMetric
import org.graphoenix.server.domain.metric.usecase.SaveMetricsRequest
import org.graphoenix.server.domain.workspace.model.WorkspaceId
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

fun SaveMetricsRequest.Metric.toEntity(recordingDate: LocalDateTime): TaskRunnerMetricEntity =
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
