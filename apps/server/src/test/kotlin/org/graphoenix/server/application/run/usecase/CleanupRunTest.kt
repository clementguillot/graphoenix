package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.run.entity.*
import org.graphoenix.server.domain.run.gateway.*
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class CleanupRunTest {
  @MockK
  private lateinit var mockRunRepository: RunRepository

  @MockK
  private lateinit var mockTaskRepository: TaskRepository

  @MockK
  private lateinit var mockArtifactRepository: ArtifactRepository

  @MockK
  private lateinit var mockStorageService: StorageService

  @InjectMockKs
  private lateinit var cleanupRun: CleanupRun

  @Test
  fun `should purge older runs and their data`() =
    runTest {
      val thresholdDate = LocalDateTime.now().minusDays(31)

      val dummyRuns = listOf(buildRun(thresholdDate))
      val dummyTasks = listOf(buildTask(dummyRuns[0].id, dummyRuns[0].workspaceId), buildTask(dummyRuns[0].id, dummyRuns[0].workspaceId))
      val dummyArtifacts =
        listOf(
          buildArtifact(dummyTasks[0].hash, dummyTasks[0].workspaceId, dummyTasks[0].artifactId!!),
          buildArtifact(dummyTasks[1].hash, dummyTasks[1].workspaceId, dummyTasks[1].artifactId!!),
        )

      coEvery { mockRunRepository.findAllByCreationDateOlderThan(thresholdDate) } returns flowOf(*dummyRuns.toTypedArray())
      coEvery { mockTaskRepository.findAllByRunId(any()) } returns flowOf(*dummyTasks.toTypedArray())

      coEvery { mockStorageService.deleteArtifact(any(), dummyRuns[0].workspaceId) } just runs
      coEvery { mockArtifactRepository.delete(any()) } returns true
      coEvery { mockTaskRepository.deleteAllByRunId(any()) } returns 2L
      coEvery { mockRunRepository.delete(match { dummyRuns.contains(it) }) } returns true

      val result =
        cleanupRun(request = CleanupRunRequest(creationDateThreshold = thresholdDate)) {
          it
        }

      expect(result.deletedCount).toEqual(1)

      coVerify(exactly = 1) { mockStorageService.deleteArtifact(dummyArtifacts[0].id, dummyRuns[0].workspaceId) }
      coVerify(exactly = 1) { mockStorageService.deleteArtifact(dummyArtifacts[1].id, dummyRuns[0].workspaceId) }
      coVerify(exactly = 2) { mockArtifactRepository.delete(any()) }
      coVerify(exactly = 1) { mockTaskRepository.deleteAllByRunId(dummyRuns[0].id) }
      coVerify(exactly = 1) { mockRunRepository.delete(dummyRuns[0]) }
    }

  private fun buildRun(endTime: LocalDateTime = LocalDateTime.now()): Run =
    Run {
      id(UUID.randomUUID().toString())
      workspaceId(UUID.randomUUID().toString())
      command = "test command"
      status = 0
      startTime = LocalDateTime.now()
      this.endTime = endTime
      runGroup = "group"
      inner = true
      machineInfo = MachineInfo("machineId", "platform", "version", 4)
      meta = emptyMap()
      linkId = LinkId("link-id")
    }

  private fun buildTask(
    runId: RunId,
    workspaceId: WorkspaceId,
  ): Task =
    Task {
      taskId = TaskId(UUID.randomUUID().toString())
      this.runId = runId
      this.workspaceId = workspaceId
      hash = Hash(UUID.randomUUID().toString())
      projectName = "project name"
      target = "target"
      startTime = LocalDateTime.now()
      endTime = LocalDateTime.now()
      cacheStatus = CacheStatus.CACHE_MISS
      status = 0
      uploadedToStorage = true
      terminalOutputUploadedToFileStorage = true
      isCacheable = true
      parallelism = true
      params = "params"
      terminalOutput = "terminal output"
      hashDetails = HashDetails(emptyMap(), emptyMap(), emptyMap())
      artifactId = ArtifactId()
      meta = null
    }

  private fun buildArtifact(
    hash: Hash,
    workspaceId: WorkspaceId,
    id: ArtifactId = ArtifactId(),
  ): Artifact.Exist =
    Artifact.Exist(
      id = id,
      hash = hash,
      workspaceId = workspaceId,
      put = "put-url",
      get = "get-url",
    )
}
