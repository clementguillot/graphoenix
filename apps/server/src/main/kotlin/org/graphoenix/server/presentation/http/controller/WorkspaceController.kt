package org.graphoenix.server.presentation.http.controller

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.workspace.usecase.CreateOrgAndWorkspace
import org.graphoenix.server.application.workspace.usecase.CreateWorkspace
import org.graphoenix.server.configuration.ServerConfiguration
import org.graphoenix.server.presentation.http.controller.dto.CreateOrgAndWorkspaceDto
import org.graphoenix.server.presentation.http.controller.dto.CreateWorkspaceDto
import org.graphoenix.server.presentation.http.controller.dto.IdDto
import org.graphoenix.server.presentation.http.controller.dto.InitWorkspaceDto

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class WorkspaceController(
  private val serverConfiguration: ServerConfiguration,
  private val createWorkspace: CreateWorkspace,
  private val createOrgAndWorkspace: CreateOrgAndWorkspace,
) {
  @Operation(summary = "Create a public cloud counterpart for a private cloud workspace")
  @POST
  @Path("/private/create-workspace")
  suspend fun create(workspaceDto: CreateWorkspaceDto) =
    createWorkspace(workspaceDto.toRequest()) { response ->
      IdDto(response.workspace.id.value)
    }

  @Operation(summary = "Create an org and a workspace")
  @POST
  @Path("/create-org-and-workspace")
  suspend fun createOrgAndWorkspace(requestDto: CreateOrgAndWorkspaceDto) =
    createOrgAndWorkspace(
      requestDto.toRequest(),
    ) { response ->
      InitWorkspaceDto(
        url = "${serverConfiguration.applicationUrl()}?token=${response.accessToken.encodedValue}",
        token = response.accessToken.encodedValue,
      )
    }
}
