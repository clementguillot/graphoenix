package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.TaskEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@QuarkusTest
class TaskPanacheRepositoryTest {
  @Inject
  lateinit var taskPanacheRepository: TaskPanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      taskPanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should create new tasks in the DB`() =
    runTest {
      // Given
      val tasks =
        listOf(
          CreateTaskCommand(
            taskId = "task1",
            hash = Hash("hash1"),
            projectName = "project1",
            target = "target1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            cacheStatus = CacheStatus.CACHE_MISS,
            status = 0,
            uploadedToStorage = true,
            terminalOutputUploadedToFileStorage = true,
            isCacheable = true,
            parallelism = true,
            params = "params1",
            terminalOutput = "output1",
            hashDetails =
              HashDetails(
                nodes = mapOf("apps/server:ProjectConfiguration" to "dummy"),
                runtime = emptyMap(),
                implicitDeps = emptyMap(),
              ),
            artifactId = ArtifactId("artifact1"),
            meta = null,
          ),
          CreateTaskCommand(
            taskId = "task2",
            hash = Hash("hash2"),
            projectName = "project2",
            target = "target2",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            cacheStatus = CacheStatus.LOCAL_CACHE_HIT,
            status = 1,
            uploadedToStorage = true,
            terminalOutputUploadedToFileStorage = true,
            isCacheable = true,
            parallelism = true,
            params = "params2",
            terminalOutput = "output2",
            hashDetails =
              HashDetails(
                nodes = mapOf("apps/server:ProjectConfiguration" to "dummy"),
                runtime = emptyMap(),
                implicitDeps = emptyMap(),
              ),
            artifactId = null,
            meta =
              Metadata(
                description = null,
                technologies = null,
                targetGroups = null,
              ),
          ),
        )
      val runId = RunId(ObjectId().toString())
      val workspaceId = WorkspaceId(ObjectId().toString())

      // When
      val result = taskPanacheRepository.create(tasks, runId, workspaceId)

      // Then
      expect(result.size).toEqual(tasks.size)
      expect(taskPanacheRepository.count().awaitSuspending()).toEqual(2L)
    }

  @Test
  fun `should find tasks by their run ID`() =
    runTest {
      // Given
      val dummyRunId = ObjectId()
      val dummyTaskEntities = listOf(buildTaskEntity(dummyRunId))
      taskPanacheRepository.persist(dummyTaskEntities).awaitSuspending()

      // When
      val result = taskPanacheRepository.findAllByRunId(RunId(dummyRunId.toString())).toList()

      // Then
      expect(result.size).toEqual(1)
      expect(result.toList()[0].taskId.value).toEqual(dummyTaskEntities[0].taskId)
    }

  @Test
  fun `should delete tasks by their run ID`() =
    runTest {
      // Given
      val dummyRunId = ObjectId()
      val dummyTaskEntities = listOf(buildTaskEntity(dummyRunId))
      taskPanacheRepository.persist(dummyTaskEntities).awaitSuspending()

      // When
      val result = taskPanacheRepository.deleteAllByRunId(RunId(dummyRunId.toString()))

      // Then
      expect(result).toEqual(1)
    }

  private fun buildTaskEntity(runId: ObjectId = ObjectId()): TaskEntity =
    TaskEntity(
      id = null,
      runId = runId,
      workspaceId = ObjectId(),
      taskId = "task123",
      hash = "hash123",
      projectName = "project",
      target = "target",
      startTime = LocalDateTime.now(),
      endTime = LocalDateTime.now(),
      cacheStatus = "cache-miss",
      status = 1,
      uploadedToStorage = true,
      terminalOutputUploadedToFileStorage = true,
      isCacheable = true,
      parallelism = true,
      params = "params",
      terminalOutput = "output",
      hashDetails =
        TaskEntity.HashDetails(
          nodes = mapOf("node1" to "hash1", "node2" to "hash2"),
          runtime = mapOf("runtime1" to "hash1", "runtime2" to "hash2"),
          implicitDeps = mapOf("dep1" to "hash1", "dep2" to "hash2"),
        ),
      artifactId = null,
      meta = null,
    )
}
