package org.graphoenix.server.persistence.mapper

import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.WorkspaceEntity

fun WorkspaceEntity.toDomain(): Workspace =
  Workspace(
    id = WorkspaceId(id.toString()),
    orgId = OrganizationId(orgId.toString()),
    name = name,
    installationSource = installationSource,
    nxInitDate = nxInitDate,
  )
