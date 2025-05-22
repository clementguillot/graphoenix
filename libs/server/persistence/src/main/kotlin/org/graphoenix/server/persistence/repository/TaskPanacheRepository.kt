package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.asFlow
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.graphoenix.server.domain.common.pagination.PageCollection
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
  override fun findAllByRunId(runId: RunId): Flow<Task> =
    find(TaskEntity::runId.name, ObjectId(runId.value)).stream().asFlow().map {
      it.toDomain()
    }

  override suspend fun findPageByRunIdAndWorkspaceId(
    runId: RunId,
    workspaceId: WorkspaceId,
    pageIndex: Int,
    pageSize: Int,
  ): PageCollection<Task> =
    coroutineScope {
      find(
        "runId = ?1 and workspaceId = ?2",
        Sort.by("endTime", Sort.Direction.Descending),
        ObjectId(runId.value),
        ObjectId(workspaceId.value),
      ).page(Page(pageIndex, pageSize))
        .let { query ->
          val result = async { query.list().awaitSuspending() }
          val totalCount = async { query.count().awaitSuspending() }
          PageCollection(
            items = result.await().map { it.toDomain() },
            totalCount = totalCount.await(),
          )
        }
    }

  override suspend fun create(
    tasks: Collection<CreateTaskCommand>,
    runId: RunId,
    workspaceId: WorkspaceId,
  ): Collection<Task> {
    val entities = tasks.map { it.toEntity(runId, workspaceId) }

    persist(entities).awaitSuspending()

    return entities.map { it.toDomain() }
  }

  override suspend fun deleteAllByRunId(runId: RunId): Long = delete(TaskEntity::runId.name, ObjectId(runId.value)).awaitSuspending()
}
