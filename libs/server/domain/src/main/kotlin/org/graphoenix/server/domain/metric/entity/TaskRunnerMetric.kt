package org.graphoenix.server.domain.metric.entity

import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import java.time.LocalDateTime

class TaskRunnerMetric private constructor(
  builder: Builder,
) {
  companion object {
    operator fun invoke(block: Builder.() -> Unit): TaskRunnerMetric {
      val builder = Builder()
      block(builder)
      return builder.build()
    }
  }

  val workspaceId: WorkspaceId
  val recordingDate: LocalDateTime
  val durationMs: Int
  val success: Boolean
  val statusCode: Int
  val entryType: String
  val payloadSize: Long?

  init {
    requireNotNull(builder.workspaceId)
    requireNotNull(builder.recordingDate)
    requireNotNull(builder.durationMs)
    requireNotNull(builder.success)
    requireNotNull(builder.statusCode)
    requireNotNull(builder.entryType)

    workspaceId = builder.workspaceId!!
    recordingDate = builder.recordingDate!!
    durationMs = builder.durationMs!!
    success = builder.success!!
    statusCode = builder.statusCode!!
    entryType = builder.entryType!!
    payloadSize = builder.payloadSize
  }

  class Builder {
    var workspaceId: WorkspaceId? = null
    var recordingDate: LocalDateTime? = null
    var durationMs: Int? = null
    var success: Boolean? = null
    var statusCode: Int? = null
    var entryType: String? = null
    var payloadSize: Long? = null

    fun build(): TaskRunnerMetric = TaskRunnerMetric(this)
  }
}
