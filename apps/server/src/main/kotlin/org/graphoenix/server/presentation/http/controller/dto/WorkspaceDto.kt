package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.domain.workspace.entity.Workspace

data class WorkspaceDto(
  val id: String,
  val name: String,
  val orgId: String,
) {
  companion object {
    fun from(workspace: Workspace) =
      WorkspaceDto(
        id = workspace.id.value,
        name = workspace.name,
        orgId = workspace.orgId.value,
      )
  }
}
