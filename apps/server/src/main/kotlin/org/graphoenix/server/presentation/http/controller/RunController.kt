package org.graphoenix.server.presentation.http.controller

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.CurrentIdentityAssociation
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.run.usecase.EndRun
import org.graphoenix.server.application.run.usecase.EndRunRequest
import org.graphoenix.server.application.run.usecase.StartRun
import org.graphoenix.server.application.run.usecase.StartRunRequest
import org.graphoenix.server.configuration.ServerConfiguration
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.AccessLevel
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.infrastructure.io.GzipJsonDecoder
import org.graphoenix.server.presentation.http.controller.dto.RemoteArtifactListDto
import org.graphoenix.server.presentation.http.controller.dto.RunDto
import org.graphoenix.server.presentation.http.controller.dto.RunSummaryDto
import org.graphoenix.server.presentation.http.getWorkspaceId

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
class RunController(
  private val gzipJsonDecoder: GzipJsonDecoder,
  private val identity: CurrentIdentityAssociation,
  private val serverConfiguration: ServerConfiguration,
  private val startRun: StartRun,
  private val endRun: EndRun,
) {
  @Operation(
    summary = "Retrieves URLs for retrieving/storing cached artifacts",
  )
  @POST
  @Path("/v2/runs/start")
  @Consumes(MediaType.APPLICATION_JSON)
  suspend fun start(startRunDto: RunDto.Start): RemoteArtifactListDto =
    startRun(
      StartRunRequest(
        hashes = startRunDto.hashes.map { Hash(it) },
        workspaceId =
          WorkspaceId(
            identity.deferredIdentity
              .awaitSuspending()
              .principal.name,
          ),
        canPut = isReadWriteContext(),
      ),
    ) { response ->
      RemoteArtifactListDto.from(response)
    }

  @Operation(
    summary = "Stores information about a run",
  )
  @POST
  @Path("/runs/end")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  suspend fun end(request: ByteArray): RunSummaryDto =
    gzipJsonDecoder
      .from(request, RunDto.End::class)
      .let { dto ->
        endRun(
          EndRunRequest(
            run = dto.toRunRequest(),
            tasks = dto.toTaskRequests(),
            workspaceId = identity.getWorkspaceId(),
          ),
        ) { response ->
          RunSummaryDto.from(response, serverConfiguration.applicationUrl())
        }
      }

  @Operation(
    summary = "Indicates if the workspace for the authentication token is enabled",
  )
  @GET
  @Path("/runs/workspace-status")
  @Produces(MediaType.TEXT_PLAIN)
  suspend fun workspaceStatus() = "" // hopefully, all authenticated workspaces are enabled :)

  private suspend fun isReadWriteContext(): Boolean = identity.deferredIdentity.awaitSuspending().hasRole(AccessLevel.READ_WRITE.value)
}
