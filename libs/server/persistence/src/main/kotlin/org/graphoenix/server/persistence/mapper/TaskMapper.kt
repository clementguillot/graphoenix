package org.graphoenix.server.persistence.mapper

import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.MetadataEntity
import org.graphoenix.server.persistence.entity.TaskEntity

fun TaskEntity.toDomain(): Task =
  Task {
    taskId = TaskId(this@toDomain.taskId)
    runId = RunId(this@toDomain.runId.toString())
    workspaceId = WorkspaceId(this@toDomain.workspaceId.toString())
    hash = Hash(this@toDomain.hash)
    projectName = this@toDomain.projectName
    target = this@toDomain.target
    startTime = this@toDomain.startTime
    endTime = this@toDomain.endTime
    cacheStatus = this@toDomain.cacheStatus?.let { CacheStatus.from(it) }
    status = this@toDomain.status
    uploadedToStorage = this@toDomain.uploadedToStorage
    terminalOutputUploadedToFileStorage = this@toDomain.terminalOutputUploadedToFileStorage
    isCacheable = this@toDomain.isCacheable
    parallelism = this@toDomain.parallelism
    params = this@toDomain.params
    hashDetails =
      HashDetails(
        nodes = this@toDomain.hashDetails.nodes,
        runtime = this@toDomain.hashDetails.runtime,
        implicitDeps = this@toDomain.hashDetails.implicitDeps,
      )
    terminalOutput = this@toDomain.terminalOutput
    artifactId = this@toDomain.artifactId?.let { ArtifactId(it) }
    meta =
      this@toDomain.meta?.let {
        Metadata(
          description = it.description,
          technologies = it.technologies,
          targetGroups = it.targetGroups,
        )
      }
  }

fun CreateTaskCommand.toEntity(
  runId: RunId,
  workspaceId: WorkspaceId,
): TaskEntity =
  TaskEntity(
    id = null,
    runId = ObjectId(runId.value),
    workspaceId = ObjectId(workspaceId.value),
    taskId = taskId,
    hash = hash.value,
    projectName = projectName,
    target = target,
    startTime = startTime,
    endTime = endTime,
    cacheStatus = cacheStatus.value,
    status = status,
    uploadedToStorage = uploadedToStorage,
    terminalOutputUploadedToFileStorage = terminalOutputUploadedToFileStorage,
    isCacheable = isCacheable,
    parallelism = parallelism,
    params = params,
    hashDetails =
      TaskEntity.HashDetails(
        nodes = hashDetails.nodes,
        runtime = hashDetails.runtime,
        implicitDeps = hashDetails.implicitDeps,
      ),
    terminalOutput = terminalOutput,
    artifactId = artifactId?.value,
    meta =
      meta?.let {
        MetadataEntity(
          description = it.description,
          technologies = it.technologies,
          targetGroups = it.targetGroups,
        )
      },
  )
