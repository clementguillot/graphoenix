package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.domain.workspace.entity.Organization

@RegisterForReflection
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
