package org.graphoenix.server.domain.run.gateway

import kotlinx.coroutines.flow.Flow
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.valueobject.RunStatus
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import java.time.LocalDateTime

interface RunRepository {
  suspend fun create(
    run: CreateRunCommand,
    status: RunStatus,
    workspaceId: WorkspaceId,
  ): Run

  fun findAllByCreationDateOlderThan(date: LocalDateTime): Flow<Run>

  suspend fun delete(run: Run): Boolean
}
