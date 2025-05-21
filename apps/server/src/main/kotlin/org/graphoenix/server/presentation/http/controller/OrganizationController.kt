package org.graphoenix.server.presentation.http.controller

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.workspace.usecase.*
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.presentation.http.controller.dto.*
import org.jboss.resteasy.reactive.RestPath

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrganizationController(
  private val createOrganization: CreateOrganization,
  private val getAllOrganizations: GetAllOrganizations,
  private val getOrganizationById: GetOrganizationById,
) {
  @Operation(
    summary = "Create a public cloud counterpart for a private cloud org",
  )
  @POST
  @Path("/private/create-org")
  suspend fun create(orgDto: CreateOrganizationDto): IdDto =
    createOrganization(orgDto.toRequest()) { response ->
      IdDto(response.organization.id.value)
    }

  @Operation(
    summary = "Gets all organizations",
  )
  @GET
  @Path("/private/organizations")
  suspend fun list(): Collection<OrganizationDto> =
    getAllOrganizations(Unit) { response ->
      response.organizations.map { OrganizationDto.from(it) }
    }

  @Operation(
    summary = "Gets an organization by its ID",
  )
  @GET
  @Path("/private/organizations/{id}")
  suspend fun getById(
    @RestPath id: String,
  ): OrganizationDto =
    getOrganizationById(GetOrganizationById.Request(id = OrganizationId(id))) { response ->
      OrganizationDto.from(response.organization)
    }
}
