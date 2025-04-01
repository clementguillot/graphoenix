package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.domain.metric.command.CreateMetricCommand
import org.graphoenix.server.domain.metric.entity.TaskRunnerMetric
import org.graphoenix.server.domain.metric.gateway.TaskRunnerMetricRepository
import org.graphoenix.server.persistence.entity.TaskRunnerMetricEntity
import org.graphoenix.server.persistence.mapper.toDomain
import org.graphoenix.server.persistence.mapper.toEntity
import java.time.LocalDateTime

@ApplicationScoped
class TaskRunnerMetricPanacheRepository :
  ReactivePanacheMongoRepository<TaskRunnerMetricEntity>,
  TaskRunnerMetricRepository {
  override suspend fun save(metrics: Collection<CreateMetricCommand>): Collection<TaskRunnerMetric> {
    val recordingDate = LocalDateTime.now()
    val entities = metrics.map { it.toEntity(recordingDate) }
    persist(entities).awaitSuspending()
    return entities.map { it.toDomain() }
  }
}
