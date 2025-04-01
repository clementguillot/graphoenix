package org.graphoenix.server.domain.workspace.entity

import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import java.time.LocalDateTime

data class Workspace(
  val id: WorkspaceId,
  val orgId: OrganizationId,
  val name: String,
  val installationSource: String?,
  val nxInitDate: LocalDateTime?,
)
