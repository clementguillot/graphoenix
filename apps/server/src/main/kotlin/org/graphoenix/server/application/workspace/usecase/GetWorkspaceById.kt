package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class GetWorkspaceById(
  private val workspaceRepository: WorkspaceRepository,
) : UseCase<GetWorkspaceById.Request, GetWorkspaceById.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      workspaceRepository.findByIdAndOrgId(request.id, request.orgId)?.let { workspace ->
        Response(workspace = workspace)
      } ?: throw WorkspaceException.NotFound("Workspace ${request.id} (${request.orgId}) not found"),
    )

  data class Request(
    val id: WorkspaceId,
    val orgId: OrganizationId,
  )

  data class Response(
    val workspace: Workspace,
  )
}
