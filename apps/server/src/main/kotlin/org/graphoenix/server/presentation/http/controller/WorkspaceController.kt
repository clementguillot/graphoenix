package org.graphoenix.server.presentation.http.controller

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.CurrentIdentityAssociation
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.workspace.usecase.*
import org.graphoenix.server.configuration.ServerConfiguration
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.presentation.http.controller.dto.*
import org.jboss.resteasy.reactive.RestPath

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WorkspaceController(
  private val identity: CurrentIdentityAssociation,
  private val serverConfiguration: ServerConfiguration,
  private val createWorkspace: CreateWorkspace,
  private val createOrgAndWorkspace: CreateOrgAndWorkspace,
  private val getWorkspacesByOrgId: GetWorkspacesByOrgId,
  private val getWorkspaceById: GetWorkspaceById,
) {
  @Operation(summary = "Create a public cloud counterpart for a private cloud workspace")
  @POST
  @Path("/private/create-workspace")
  suspend fun create(workspaceDto: CreateWorkspaceDto): IdDto =
    createWorkspace(workspaceDto.toRequest()) { response ->
      IdDto(response.workspace.id.value)
    }

  @Operation(summary = "Create an org and a workspace")
  @POST
  @Path("/create-org-and-workspace")
  suspend fun createOrgAndWorkspace(requestDto: CreateOrgAndWorkspaceDto): InitWorkspaceDto =
    createOrgAndWorkspace(
      requestDto.toRequest(),
    ) { response ->
      InitWorkspaceDto(
        url = "${serverConfiguration.applicationUrl()}?token=${response.accessToken.encodedValue}",
        token = response.accessToken.encodedValue,
      )
    }

  @Operation(
    summary = "Gets all workspaces for an organization",
  )
  @GET
  @Path("/private/organizations/{orgId}/workspaces")
  suspend fun getWorkspacesByOrg(
    @RestPath orgId: String,
  ): Collection<WorkspaceDto> =
    getWorkspacesByOrgId(GetWorkspacesByOrgId.Request(orgId = OrganizationId(orgId))) { response ->
      response.workspaces.map { WorkspaceDto.from(it) }
    }

  @Operation(
    summary = "Gets a workspace by its ID and org ID",
  )
  @GET
  @Path("/private/organizations/{orgId}/workspaces/{workspaceId}")
  suspend fun getById(
    @RestPath orgId: String,
    @RestPath workspaceId: String,
  ): WorkspaceDto =
    getWorkspaceById(
      GetWorkspaceById.Request(
        id = WorkspaceId(workspaceId),
        orgId = OrganizationId(orgId),
      ),
    ) { response ->
      WorkspaceDto.from(response.workspace)
    }

  @GET
  @Authenticated
  @Path("/private/workspaces/me")
  suspend fun me(): String =
    identity.deferredIdentity
      .awaitSuspending()
      .principal.name
}
