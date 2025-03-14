package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.application.workspace.usecase.CreateOrganizationRequest

data class CreateOrganizationDto(
  val name: String,
) {
  fun toRequest() = CreateOrganizationRequest(name = name)
}
