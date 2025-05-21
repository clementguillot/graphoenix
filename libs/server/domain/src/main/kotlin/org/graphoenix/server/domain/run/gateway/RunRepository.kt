package org.graphoenix.server.domain.run.gateway

import kotlinx.coroutines.flow.Flow
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import java.time.LocalDateTime

interface RunRepository {
  fun findAllByCreationDateOlderThan(date: LocalDateTime): Flow<Run>

  suspend fun findPageByWorkspaceId(
    workspaceId: WorkspaceId,
    pageIndex: Int,
    pageSize: Int,
  ): PageCollection<Run>

  suspend fun findByLinkId(linkId: LinkId): Run?

  suspend fun create(
    run: CreateRunCommand,
    status: Int,
    workspaceId: WorkspaceId,
  ): Run

  suspend fun delete(run: Run): Boolean
}
