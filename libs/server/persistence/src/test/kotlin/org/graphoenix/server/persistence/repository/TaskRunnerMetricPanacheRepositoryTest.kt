package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class TaskRunnerMetricPanacheRepositoryTest {
  @Inject
  lateinit var taskRunnerMetricPanacheRepository: TaskRunnerMetricPanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      taskRunnerMetricPanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should create new task runner metrics in the DB`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId(ObjectId().toString())
      val dummyMetrics =
        listOf(
          CreateMetricCommand(
            workspaceId = dummyWorkspaceId,
            entryType = "createRunGroup",
            success = true,
            statusCode = 200,
            durationMs = 20,
            payloadSize = null,
          ),
          CreateMetricCommand(
            workspaceId = dummyWorkspaceId,
            entryType = "completeRunGroup",
            success = true,
            statusCode = 200,
            durationMs = 20,
            payloadSize = null,
          ),
        )

      // When
      val result = taskRunnerMetricPanacheRepository.save(dummyMetrics)

      // Then
      expect(result.size).toEqual(dummyMetrics.size)
      expect(taskRunnerMetricPanacheRepository.count().awaitSuspending()).toEqual(2L)
    }
}
