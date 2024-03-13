package org.nxcloudce.api.domain.run.interactor

import ch.tutteli.atrium.api.fluent.en_GB.notToEqualNull
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.nxcloudce.api.domain.run.gateway.ArtifactRepository
import org.nxcloudce.api.domain.run.gateway.RunRepository
import org.nxcloudce.api.domain.run.gateway.TaskRepository
import org.nxcloudce.api.domain.run.model.*
import org.nxcloudce.api.domain.run.usecase.EndRunRequest
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import java.time.LocalDateTime

@QuarkusTest
class EndRunImplTest {
  @InjectMock
  lateinit var mockRunRepository: RunRepository

  @InjectMock
  lateinit var mockTaskRepository: TaskRepository

  @InjectMock
  lateinit var mockArtifactRepository: ArtifactRepository

  @Inject
  lateinit var endRunImpl: EndRunImpl

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
      val response = endRunImpl.end(request) { it.run }

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
      val response = endRunImpl.end(request) { it.run }

      // Then
      expect(response.status).toEqual(RunStatus.FAILURE)
    }

  private fun buildRequestRun(): EndRunRequest.Run =
    EndRunRequest.Run(
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
      vcsContext = "test vcs context",
      linkId = "test link id",
      projectGraph = "project graph",
      hashedContributors = null,
    )

  private fun buildRequestTask(
    suffix: String,
    uploadedToStorage: Boolean,
    status: Int = 0,
  ): EndRunRequest.Task =
    EndRunRequest.Task(
      taskId = "task$suffix",
      hash = Hash("hash$suffix"),
      projectName = "project$suffix",
      target = "target$suffix",
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now(),
      cacheStatus = CacheStatus.CACHE_MISS,
      status = status,
      uploadedToStorage = uploadedToStorage,
      params = "test params",
      terminalOutput = "test terminal output",
      hashDetails =
        HashDetails(
          nodes = mapOf("apps/api:ProjectConfiguration" to "dummy"),
          runtime = emptyMap(),
          implicitDeps = emptyMap(),
        ),
      artifactId = ArtifactId("artifact$suffix"),
    )

  private fun runRequestToDomain(
    run: EndRunRequest.Run,
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
      tasks = emptyList()
      linkId = run.linkId
    }

  private fun taskRequestToDomain(
    task: EndRunRequest.Task,
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
      params = task.params
      terminalOutput = task.terminalOutput
      hashDetails = task.hashDetails
      artifactId = task.artifactId
    }
}
