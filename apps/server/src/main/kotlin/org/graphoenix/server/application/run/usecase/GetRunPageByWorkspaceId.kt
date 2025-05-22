package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class GetRunPageByWorkspaceId(
  private val runRepository: RunRepository,
) : UseCase<GetRunPageByWorkspaceId.Request, GetRunPageByWorkspaceId.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      Response(
        runs =
          runRepository.findPageByWorkspaceId(
            request.workspaceId,
            request.pageIndex,
            request.pageSize,
          ),
      ),
    )

  data class Request(
    val workspaceId: WorkspaceId,
    val pageIndex: Int,
    val pageSize: Int,
  )

  data class Response(
    val runs: PageCollection<Run>,
  )
}
