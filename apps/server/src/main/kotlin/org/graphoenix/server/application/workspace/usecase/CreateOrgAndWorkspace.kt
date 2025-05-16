package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.*
import org.graphoenix.server.infrastructure.persistence.withTransaction
import java.time.LocalDateTime

@ApplicationScoped
class CreateOrgAndWorkspace(
  private val workspaceRepository: WorkspaceRepository,
  private val organizationRepository: OrganizationRepository,
  private val accessTokenRepository: AccessTokenRepository,
) : UseCase<CreateOrgAndWorkspaceRequest, CreateOrgAndWorkspaceResponse> {
  override suspend operator fun <T> invoke(
    request: CreateOrgAndWorkspaceRequest,
    presenter: (CreateOrgAndWorkspaceResponse) -> T,
  ): T =
    presenter(
      withTransaction {
        val org = organizationRepository.create(request.workspaceName)
        val workspace =
          workspaceRepository.create(
            request.workspaceName,
            request.installationSource,
            request.nxInitDate,
            org.id,
          )
        val accessToken = accessTokenRepository.createDefaultAccessToken(workspace.id)
        CreateOrgAndWorkspaceResponse(workspace, accessToken)
      },
    )
}

data class CreateOrgAndWorkspaceRequest(
  val workspaceName: String,
  val installationSource: String,
  val nxInitDate: LocalDateTime?,
)

data class CreateOrgAndWorkspaceResponse(
  val workspace: Workspace,
  val accessToken: AccessToken,
)
