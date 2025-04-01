package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.asFlow
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.graphoenix.server.domain.run.valueobject.RunId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.TaskEntity
import org.graphoenix.server.persistence.mapper.toDomain
import org.graphoenix.server.persistence.mapper.toEntity

@ApplicationScoped
class TaskPanacheRepository :
  ReactivePanacheMongoRepository<TaskEntity>,
  TaskRepository {
  override suspend fun create(
    tasks: Collection<CreateTaskCommand>,
    runId: RunId,
    workspaceId: WorkspaceId,
  ): Collection<Task> {
    val entities = tasks.map { it.toEntity(runId, workspaceId) }

    persist(entities).awaitSuspending()

    return entities.map { it.toDomain() }
  }

  override fun findAllByRunId(runId: RunId): Flow<Task> =
    find(TaskEntity::runId.name, ObjectId(runId.value)).stream().asFlow().map {
      it.toDomain()
    }

  override suspend fun deleteAllByRunId(runId: RunId): Long = delete(TaskEntity::runId.name, ObjectId(runId.value)).awaitSuspending()
}
