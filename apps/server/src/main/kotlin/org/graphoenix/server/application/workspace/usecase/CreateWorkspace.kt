package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

@ApplicationScoped
class CreateWorkspace(
  private val workspaceGateway: WorkspaceRepository,
  private val organizationRepository: OrganizationRepository,
) : UseCase<CreateWorkspaceRequest, CreateWorkspaceResponse> {
  override suspend operator fun <T> invoke(
    request: CreateWorkspaceRequest,
    presenter: (CreateWorkspaceResponse) -> T,
  ): T {
    if (!organizationRepository.isValidOrgId(request.orgId)) {
      throw WorkspaceException.OrganizationNotFound("Organization ID '${request.orgId}' not found")
    }
    val workspace = workspaceGateway.create(request.name, request.orgId)
    return presenter(CreateWorkspaceResponse(workspace))
  }
}

data class CreateWorkspaceRequest(
  val orgId: OrganizationId,
  val name: String,
)

data class CreateWorkspaceResponse(
  val workspace: Workspace,
)
