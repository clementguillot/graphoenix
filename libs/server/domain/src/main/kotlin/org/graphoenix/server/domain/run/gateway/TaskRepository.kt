package org.graphoenix.server.domain.run.gateway

import kotlinx.coroutines.flow.Flow
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.command.CreateTaskCommand
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.valueobject.RunId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

interface TaskRepository {
  fun findAllByRunId(runId: RunId): Flow<Task>

  suspend fun findPageByRunIdAndWorkspaceId(
    runId: RunId,
    workspaceId: WorkspaceId,
    pageIndex: Int,
    pageSize: Int,
  ): PageCollection<Task>

  suspend fun create(
    tasks: Collection<CreateTaskCommand>,
    runId: RunId,
    workspaceId: WorkspaceId,
  ): Collection<Task>

  suspend fun deleteAllByRunId(runId: RunId): Long
}
