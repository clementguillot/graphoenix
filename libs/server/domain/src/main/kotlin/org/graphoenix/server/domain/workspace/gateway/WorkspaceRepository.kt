package org.graphoenix.server.domain.workspace.gateway

import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import java.time.LocalDateTime

interface WorkspaceRepository {
  suspend fun create(
    name: String,
    orgId: OrganizationId,
  ): Workspace

  suspend fun create(
    name: String,
    installationSource: String,
    nxInitDate: LocalDateTime?,
    orgId: OrganizationId,
  ): Workspace
}
