package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

@ApplicationScoped
class GetWorkspacesByOrgId(
  private val workspaceRepository: WorkspaceRepository,
) : UseCase<GetWorkspacesByOrgId.Request, GetWorkspacesByOrgId.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      Response(workspaces = workspaceRepository.findAllByOrgId(request.orgId)),
    )

  data class Request(
    val orgId: OrganizationId,
  )

  data class Response(
    val workspaces: Collection<Workspace>,
  )
}
