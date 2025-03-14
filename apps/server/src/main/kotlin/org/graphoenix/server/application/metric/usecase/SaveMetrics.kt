package org.graphoenix.server.application.metric.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.metric.entity.TaskRunnerMetric
import org.graphoenix.server.domain.metric.gateway.TaskRunnerMetricRepository

@ApplicationScoped
class SaveMetrics(
  private val taskRunnerMetricRepository: TaskRunnerMetricRepository,
) : UseCase<SaveMetricsRequest, SaveMetricsResponse> {
  override suspend fun <T> invoke(
    request: SaveMetricsRequest,
    presenter: (SaveMetricsResponse) -> T,
  ): T =
    presenter(
      SaveMetricsResponse(
        metrics = taskRunnerMetricRepository.save(request.metrics),
      ),
    )
}

data class SaveMetricsRequest(
  val metrics: Collection<CreateMetricCommand>,
)

data class SaveMetricsResponse(
  val metrics: Collection<TaskRunnerMetric>,
)
