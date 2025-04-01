package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.application.workspace.usecase.CreateWorkspaceRequest
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

data class CreateWorkspaceDto(
  val orgId: String,
  val name: String,
) {
  fun toRequest() = CreateWorkspaceRequest(orgId = OrganizationId(orgId), name = name)
}
