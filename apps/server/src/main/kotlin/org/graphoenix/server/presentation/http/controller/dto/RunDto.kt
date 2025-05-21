package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.valueobject.*
import java.time.LocalDateTime
import java.util.*

sealed class RunDto {
  abstract val branch: String?
  abstract val runGroup: String
  abstract val ciExecutionId: String?
  abstract val ciExecutionEnv: String?
  abstract val machineInfo: MachineInfo
  abstract val meta: Map<String, String>
  abstract val vcsContext: VcsContext?
  abstract val clientInstanceId: UUID
  abstract val clientInstanceSource: String

  data class Start(
    override val branch: String?,
    override val runGroup: String,
    override val ciExecutionId: String?,
    override val ciExecutionEnv: String?,
    override val machineInfo: MachineInfo,
    override val meta: Map<String, String>,
    override val vcsContext: VcsContext?,
    override val clientInstanceId: UUID,
    override val clientInstanceSource: String,
    val distributedExecutionId: String?,
    val hashes: Collection<String>,
  ) : RunDto()

  data class End(
    override val branch: String?,
    override val runGroup: String,
    override val ciExecutionId: String?,
    override val ciExecutionEnv: String?,
    override val machineInfo: MachineInfo,
    override val meta: Map<String, String>,
    override val vcsContext: VcsContext?,
    override val clientInstanceId: UUID,
    override val clientInstanceSource: String,
    val tasks: Collection<Task>,
    val linkId: String?,
    val run: RunData,
    val projectGraph: ProjectGraph?,
    val projectGraphSha: String?,
    val hashedContributors: Collection<String>?,
  ) : RunDto() {
    companion object {
      fun buildLinkId(): String {
        val characters = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10).map { characters.random() }.joinToString("")
      }
    }

    fun toRunRequest(): CreateRunCommand =
      CreateRunCommand(
        command = run.command,
        startTime = run.startTime,
        endTime = run.endTime,
        branch = branch,
        runGroup = runGroup,
        inner = run.inner,
        distributedExecutionId = run.distributedExecutionId,
        ciExecutionId = ciExecutionId,
        ciExecutionEnv = ciExecutionEnv,
        machineInfo = machineInfo,
        meta = meta,
        vcsContext = vcsContext,
        linkId = LinkId(linkId ?: buildLinkId()),
        projectGraph = projectGraph,
        hashedContributors = hashedContributors,
        sha = run.sha,
      )

    fun toTaskRequests(runEndTime: LocalDateTime): List<CreateTaskCommand> =
      tasks.map { task ->
        CreateTaskCommand(
          taskId = task.taskId,
          hash = Hash(task.hash),
          projectName = task.projectName,
          target = task.target,
          startTime = task.startTime,
          endTime = task.endTime ?: runEndTime,
          cacheStatus = task.cacheStatus?.let { CacheStatus.from(it) } ?: CacheStatus.CACHE_MISS,
          status = task.status ?: 2,
          uploadedToStorage = task.uploadedToStorage,
          terminalOutputUploadedToFileStorage = task.terminalOutputUploadedToFileStorage,
          isCacheable = task.isCacheable,
          parallelism = task.parallelism,
          params = task.params,
          terminalOutput = task.terminalOutput,
          hashDetails = task.hashDetails,
          artifactId = task.artifactId?.let { ArtifactId(it.toString()) },
          meta = task.meta,
        )
      }

    data class Task(
      val taskId: String,
      val hash: String,
      val projectName: String,
      val target: String,
      val startTime: LocalDateTime,
      val endTime: LocalDateTime?,
      val cacheStatus: String?,
      val status: Int?,
      val uploadedToStorage: Boolean,
      val terminalOutputUploadedToFileStorage: Boolean,
      val isCacheable: Boolean,
      val parallelism: Boolean,
      val params: String,
      val terminalOutput: String?,
      val hashDetails: HashDetails,
      val artifactId: UUID?,
      val meta: Metadata?,
    )

    data class RunData(
      val command: String,
      val startTime: LocalDateTime,
      val endTime: LocalDateTime,
      val branch: String?,
      val runGroup: String?,
      val inner: Boolean,
      val distributedExecutionId: String?,
      val sha: String?,
    )
  }
}
