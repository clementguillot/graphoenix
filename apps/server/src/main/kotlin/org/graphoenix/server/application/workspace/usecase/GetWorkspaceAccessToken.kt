package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.gateway.AccessTokenRepository

@ApplicationScoped
class GetWorkspaceAccessToken(
  private val accessTokenRepository: AccessTokenRepository,
) : UseCase<GetWorkspaceAccessTokenRequest, GetWorkspaceAccessTokenResponse> {
  override suspend operator fun <T> invoke(
    request: GetWorkspaceAccessTokenRequest,
    presenter: (GetWorkspaceAccessTokenResponse) -> T,
  ): T =
    accessTokenRepository.findByEncodedValue(request.encodedAccessToken).let { token ->
      presenter(GetWorkspaceAccessTokenResponse(accessToken = token))
    }
}

data class GetWorkspaceAccessTokenRequest(
  val encodedAccessToken: String,
)

data class GetWorkspaceAccessTokenResponse(
  val accessToken: AccessToken?,
)
