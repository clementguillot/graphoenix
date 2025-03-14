package org.graphoenix.server.infrastructure.http.filter

import io.quarkus.runtime.Startup
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.SecurityContext
import org.graphoenix.server.application.workspace.usecase.GetWorkspaceAccessToken
import org.graphoenix.server.application.workspace.usecase.GetWorkspaceAccessTokenRequest
import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.jboss.resteasy.reactive.server.ServerRequestFilter
import java.security.Principal

@Startup
class SecurityContextFilter(
  private val getWorkspaceAccessToken: GetWorkspaceAccessToken,
) {
  @ServerRequestFilter(preMatching = true)
  suspend fun preMatchingFilter(requestContext: ContainerRequestContext) {
    val apiKey = requestContext.headers.getFirst("authorization")
    if (apiKey.isNullOrEmpty()) {
      return
    }

    getWorkspaceAccessToken(GetWorkspaceAccessTokenRequest(encodedAccessToken = apiKey)) { response ->
      response.accessToken?.let { accessToken ->
        requestContext.securityContext = buildSecurityContext(accessToken)
      }
    }
  }

  private fun buildSecurityContext(accessToken: AccessToken) =
    object : SecurityContext {
      val accessLevel = accessToken.accessLevel.value

      override fun getUserPrincipal(): Principal = Principal { accessToken.workspaceId.value }

      override fun isUserInRole(role: String) = accessLevel == role

      override fun isSecure() = true

      override fun getAuthenticationScheme() = "api-key"
    }
}
