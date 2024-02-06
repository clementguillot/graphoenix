package org.nxcloudce.api.persistence.gateway

import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.nxcloudce.api.domain.run.model.*
import org.nxcloudce.api.domain.run.usecase.EndRunRequest
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import org.nxcloudce.api.persistence.entity.TaskEntity
import org.nxcloudce.api.persistence.repository.TaskPanacheRepository
import java.time.LocalDateTime
import kotlin.test.Test

@QuarkusTest
class TaskRepositoryImplTest {
  @InjectMock
  lateinit var mockTaskPanacheRepository: TaskPanacheRepository

  @Inject
  lateinit var taskRepository: TaskRepositoryImpl

  @Test
  fun `should create new tasks in the DB`() =
    runTest {
      // Given
      val tasks =
        listOf(
          EndRunRequest.Task(
            taskId = "task1",
            hash = Hash("hash1"),
            projectName = "project1",
            target = "target1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            cacheStatus = CacheStatus.CACHE_MISS,
            status = 0,
            uploadedToStorage = true,
            params = "params1",
            terminalOutput = "output1",
            artifactId = ArtifactId("artifact1"),
          ),
          EndRunRequest.Task(
            taskId = "task2",
            hash = Hash("hash2"),
            projectName = "project2",
            target = "target2",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            cacheStatus = CacheStatus.LOCAL_CACHE_HIT,
            status = 1,
            uploadedToStorage = true,
            params = "params2",
            terminalOutput = "output2",
            artifactId = null,
          ),
        )
      val runId = RunId(ObjectId().toString())
      val workspaceId = WorkspaceId(ObjectId().toString())

      every { mockTaskPanacheRepository.persist(any<Collection<TaskEntity>>()) } answers {
        (firstArg<Collection<TaskEntity>>()).forEach { entity ->
          entity.id = ObjectId()
        }
        Uni.createFrom().voidItem()
      }

      // When
      val result = taskRepository.create(tasks, runId, workspaceId)

      // Then
      assertEquals(tasks.size, result.size)
    }
}