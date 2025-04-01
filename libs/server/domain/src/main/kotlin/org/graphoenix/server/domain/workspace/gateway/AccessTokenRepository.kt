package org.graphoenix.server.domain.workspace.gateway

import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

interface AccessTokenRepository {
  suspend fun createDefaultAccessToken(workspaceId: WorkspaceId): AccessToken

  suspend fun findByEncodedValue(encodedValue: String): AccessToken?
}
