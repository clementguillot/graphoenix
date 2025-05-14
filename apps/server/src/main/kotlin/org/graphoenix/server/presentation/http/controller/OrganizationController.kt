package org.graphoenix.server.presentation.http.controller

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.workspace.usecase.CreateOrganization
import org.graphoenix.server.presentation.http.controller.dto.CreateOrganizationDto
import org.graphoenix.server.presentation.http.controller.dto.IdDto

@Path("/private/create-org")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrganizationController(
  private val createOrganization: CreateOrganization,
) {
  @Operation(
    summary = "Create a public cloud counterpart for a private cloud org",
  )
  @POST
  suspend fun create(orgDto: CreateOrganizationDto) =
    createOrganization(orgDto.toRequest()) { response ->
      IdDto(response.organization.id.value)
    }
}
