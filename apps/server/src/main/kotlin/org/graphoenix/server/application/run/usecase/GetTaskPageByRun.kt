package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.application.run.exception.TaskException
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.entity.Task
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class GetTaskPageByRun(
  private val taskRepository: TaskRepository,
  private val runRepository: RunRepository,
) : UseCase<GetTaskPageByRun.Request, GetTaskPageByRun.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      runRepository.findByLinkId(request.runLinkId)?.let { run ->
        Response(
          tasks =
            taskRepository.findPageByRunIdAndWorkspaceId(
              run.id,
              request.workspaceId,
              request.pageIndex,
              request.pageSize,
            ),
        )
      } ?: throw TaskException.RunNotFound("Run ${request.runLinkId} (${request.workspaceId}) not found"),
    )

  data class Request(
    val runLinkId: LinkId,
    val workspaceId: WorkspaceId,
    val pageIndex: Int,
    val pageSize: Int,
  )

  data class Response(
    val tasks: PageCollection<Task>,
  )
}
