package org.graphoenix.server.application.metric.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.metric.entity.TaskRunnerMetric
import org.graphoenix.server.domain.metric.gateway.TaskRunnerMetricRepository
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SaveMetricsTest {
  private val mockTaskRunnerMetricRepository = mockk<TaskRunnerMetricRepository>()
  private val saveMetrics = SaveMetrics(mockTaskRunnerMetricRepository)

  @Test
  fun `should save and return task runner metrics`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val domainMetric =
        TaskRunnerMetric {
          workspaceId = dummyWorkspaceId
          recordingDate = LocalDateTime.now()
          durationMs = 42
          success = true
          statusCode = 200
          entryType = "dteStart"
          payloadSize = null
        }
      val request =
        SaveMetricsRequest(
          metrics =
            listOf(
              CreateMetricCommand(
                workspaceId = dummyWorkspaceId,
                entryType = "dteStart",
                success = true,
                durationMs = 42,
                statusCode = 200,
                payloadSize = null,
              ),
            ),
        )

      coEvery { mockTaskRunnerMetricRepository.save(request.metrics) } returns listOf(domainMetric)

      // When
      val response = saveMetrics(request) { it.metrics }

      // Then
      expect(response.size).toEqual(1)
    }
}
