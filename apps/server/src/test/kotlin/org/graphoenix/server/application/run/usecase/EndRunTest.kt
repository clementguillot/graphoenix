package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.notToEqualNull
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.entity.*
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EndRunTest {
  private val mockRunRepository = mockk<RunRepository>()
  private val mockTaskRepository = mockk<TaskRepository>()
  private val mockArtifactRepository = mockk<ArtifactRepository>()
  private val endRun = EndRun(mockRunRepository, mockTaskRepository, mockArtifactRepository)

  @Test
  fun `should return a success run if all tasks are OK`() =
    runTest {
      // Given
      val workspaceId = WorkspaceId("workspace-id")
      val requestRun = buildRequestRun()
      val requestTasks = listOf(buildRequestTask("1", true), buildRequestTask("2", false))
      val request =
        EndRunRequest(
          run = requestRun,
          tasks = requestTasks,
          workspaceId = workspaceId,
        )
      val domainRun = runRequestToDomain(requestRun, RunStatus.SUCCESS, workspaceId)
      val domainTasks = requestTasks.map { taskRequestToDomain(it, domainRun.id, workspaceId) }
      var remoteArtifactSize: Int? = null

      coEvery { mockRunRepository.create(requestRun, RunStatus.SUCCESS, workspaceId) } returns domainRun
      coEvery { mockTaskRepository.create(requestTasks, domainRun.id, workspaceId) } returns domainTasks
      coEvery { mockArtifactRepository.createRemoteArtifacts(any(), workspaceId) } answers {
        remoteArtifactSize = firstArg<Map<ArtifactId, Hash>>().size
        firstArg<Map<ArtifactId, Hash>>().map {
          Artifact.Exist(
            id = it.key,
            hash = it.value,
            workspaceId = workspaceId,
            put = null,
            get = null,
          )
        }
      }

      // When
      val response = endRun(request) { it.run }

      // Then
      expect(response.status).toEqual(RunStatus.SUCCESS)
      expect(remoteArtifactSize).notToEqualNull().toEqual(1)
    }

  @Test
  fun `should return a failed run if one task is KO`() =
    runTest {
      // Given
      val workspaceId = WorkspaceId("workspace-id")
      val requestRun = buildRequestRun()
      val requestTasks = listOf(buildRequestTask("1", true, 1), buildRequestTask("2", false))
      val request =
        EndRunRequest(
          run = requestRun,
          tasks = requestTasks,
          workspaceId = workspaceId,
        )
      val domainRun = runRequestToDomain(requestRun, RunStatus.FAILURE, workspaceId)
      val domainTasks = requestTasks.map { taskRequestToDomain(it, domainRun.id, workspaceId) }

      coEvery { mockRunRepository.create(requestRun, RunStatus.FAILURE, workspaceId) } returns domainRun
      coEvery { mockTaskRepository.create(requestTasks, domainRun.id, workspaceId) } returns domainTasks
      coEvery { mockArtifactRepository.createRemoteArtifacts(any(), workspaceId) } answers {
        firstArg<Map<ArtifactId, Hash>>().map {
          Artifact.Exist(
            id = it.key,
            hash = it.value,
            workspaceId = workspaceId,
            put = null,
            get = null,
          )
        }
      }

      // When
      val response = endRun(request) { it.run }

      // Then
      expect(response.status).toEqual(RunStatus.FAILURE)
    }

  private fun buildRequestRun(): CreateRunCommand =
    CreateRunCommand(
      command = "test command",
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now(),
      branch = "test branch",
      runGroup = "test run group",
      inner = true,
      distributedExecutionId = "test distributed execution id",
      ciExecutionId = "test ci execution id",
      ciExecutionEnv = "test ci execution env",
      machineInfo = MachineInfo("machine-id", "platform", "version", 32),
      meta = mapOf("nxCloudVersion" to "123"),
      vcsContext = null,
      linkId = "test link id",
      projectGraph = null,
      hashedContributors = null,
      sha = null,
    )

  private fun buildRequestTask(
    suffix: String,
    uploadedToStorage: Boolean,
    status: Int = 0,
  ): CreateTaskCommand =
    CreateTaskCommand(
      taskId = "task$suffix",
      hash = Hash("hash$suffix"),
      projectName = "project$suffix",
      target = "target$suffix",
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now(),
      cacheStatus = CacheStatus.from("cache-miss"),
      status = status,
      uploadedToStorage = uploadedToStorage,
      terminalOutputUploadedToFileStorage = true,
      isCacheable = true,
      parallelism = true,
      params = "test params",
      terminalOutput = "test terminal output",
      hashDetails =
        HashDetails(
          nodes = mapOf("apps/server:ProjectConfiguration" to "dummy"),
          runtime = emptyMap(),
          implicitDeps = emptyMap(),
        ),
      artifactId = ArtifactId("artifact$suffix"),
      meta = null,
    )

  private fun runRequestToDomain(
    run: CreateRunCommand,
    status: RunStatus,
    workspaceId: WorkspaceId,
  ): Run =
    Run {
      id = RunId("new-id")
      this.workspaceId = workspaceId
      command = run.command
      this.status = status
      startTime = run.startTime
      endTime = run.endTime
      branch = run.branch
      runGroup = run.runGroup
      inner = run.inner
      distributedExecutionId = run.distributedExecutionId
      ciExecutionId = run.ciExecutionId
      ciExecutionEnv = run.ciExecutionEnv
      machineInfo = run.machineInfo
      meta = run.meta
      vcsContext = run.vcsContext
      linkId = run.linkId
    }

  private fun taskRequestToDomain(
    task: CreateTaskCommand,
    runId: RunId,
    workspaceId: WorkspaceId,
  ): Task =
    Task {
      taskId = TaskId(task.taskId)
      this.runId = runId
      this.workspaceId = workspaceId
      hash = task.hash
      projectName = task.projectName
      target = task.target
      startTime = task.startTime
      endTime = task.endTime
      cacheStatus = task.cacheStatus
      status = task.status
      uploadedToStorage = task.uploadedToStorage
      terminalOutputUploadedToFileStorage = task.terminalOutputUploadedToFileStorage
      isCacheable = task.isCacheable
      parallelism = task.parallelism
      params = task.params
      terminalOutput = task.terminalOutput
      hashDetails = task.hashDetails
      artifactId = task.artifactId
      meta = task.meta
    }
}
