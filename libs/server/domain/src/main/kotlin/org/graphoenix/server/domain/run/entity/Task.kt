package org.graphoenix.server.domain.run.entity

import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import java.time.LocalDateTime

class Task private constructor(
  builder: Builder,
) {
  companion object {
    operator fun invoke(block: Builder.() -> Unit): Task {
      val builder = Builder()
      block(builder)
      return builder.build()
    }
  }

  val taskId: TaskId
  val runId: RunId
  val workspaceId: WorkspaceId
  val hash: Hash
  val projectName: String
  val target: String
  val startTime: LocalDateTime
  val endTime: LocalDateTime
  val cacheStatus: CacheStatus
  val status: Int
  val uploadedToStorage: Boolean
  val terminalOutputUploadedToFileStorage: Boolean
  val isCacheable: Boolean
  val parallelism: Boolean
  val params: String
  val terminalOutput: String
  val hashDetails: HashDetails
  val artifactId: ArtifactId?
  val meta: Map<String, String>?

  init {
    requireNotNull(builder.taskId)
    requireNotNull(builder.runId)
    requireNotNull(builder.workspaceId)
    requireNotNull(builder.hash)
    requireNotNull(builder.projectName)
    requireNotNull(builder.target)
    requireNotNull(builder.startTime)
    requireNotNull(builder.endTime)
    requireNotNull(builder.cacheStatus)
    requireNotNull(builder.status)
    requireNotNull(builder.uploadedToStorage)
    requireNotNull(builder.terminalOutputUploadedToFileStorage)
    requireNotNull(builder.isCacheable)
    requireNotNull(builder.parallelism)
    requireNotNull(builder.params)
    requireNotNull(builder.terminalOutput)
    requireNotNull(builder.hashDetails)

    taskId = builder.taskId!!
    runId = builder.runId!!
    workspaceId = builder.workspaceId!!
    hash = builder.hash!!
    projectName = builder.projectName!!
    target = builder.target!!
    startTime = builder.startTime!!
    endTime = builder.endTime!!
    cacheStatus = builder.cacheStatus!!
    status = builder.status!!
    uploadedToStorage = builder.uploadedToStorage!!
    terminalOutputUploadedToFileStorage = builder.terminalOutputUploadedToFileStorage!!
    isCacheable = builder.isCacheable!!
    parallelism = builder.parallelism!!
    params = builder.params!!
    hashDetails = builder.hashDetails!!
    terminalOutput = builder.terminalOutput!!
    artifactId = builder.artifactId
    meta = builder.meta
  }

  class Builder {
    var taskId: TaskId? = null
    var runId: RunId? = null
    var workspaceId: WorkspaceId? = null
    var hash: Hash? = null
    var projectName: String? = null
    var target: String? = null
    var startTime: LocalDateTime? = null
    var endTime: LocalDateTime? = null
    var cacheStatus: CacheStatus? = null
    var status: Int? = null
    var uploadedToStorage: Boolean? = null
    var terminalOutputUploadedToFileStorage: Boolean? = null
    var isCacheable: Boolean? = null
    var parallelism: Boolean? = null
    var params: String? = null
    var terminalOutput: String? = null
    var hashDetails: HashDetails? = null
    var artifactId: ArtifactId? = null
    var meta: Map<String, String>? = null

    fun build(): Task = Task(this)
  }
}
