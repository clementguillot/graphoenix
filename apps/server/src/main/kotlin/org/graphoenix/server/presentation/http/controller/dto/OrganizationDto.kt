package org.graphoenix.server.presentation.http.controller.dto

import org.graphoenix.server.domain.workspace.entity.Organization

data class OrganizationDto(
  val id: String,
  val name: String,
) {
  companion object {
    fun from(organization: Organization) =
      OrganizationDto(
        id = organization.id.value,
        name = organization.name,
      )
  }
}
