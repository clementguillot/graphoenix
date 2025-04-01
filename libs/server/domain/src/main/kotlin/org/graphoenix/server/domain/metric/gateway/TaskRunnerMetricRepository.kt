package org.graphoenix.server.domain.metric.gateway

import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.metric.entity.TaskRunnerMetric

fun interface TaskRunnerMetricRepository {
  suspend fun save(metrics: Collection<CreateMetricCommand>): Collection<TaskRunnerMetric>
}
