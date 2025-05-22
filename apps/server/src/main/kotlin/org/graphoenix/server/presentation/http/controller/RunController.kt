package org.graphoenix.server.presentation.http.controller

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.CurrentIdentityAssociation
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.application.run.usecase.*
import org.graphoenix.server.configuration.ServerConfiguration
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.AccessLevel
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.infrastructure.io.GzipJsonDecoder
import org.graphoenix.server.presentation.http.controller.dto.*
import org.graphoenix.server.presentation.http.getWorkspaceId
import org.jboss.resteasy.reactive.RestQuery

@Path("")
@Produces(MediaType.APPLICATION_JSON)
class RunController(
  private val gzipJsonDecoder: GzipJsonDecoder,
  private val identity: CurrentIdentityAssociation,
  private val serverConfiguration: ServerConfiguration,
  private val startRun: StartRun,
  private val endRun: EndRun,
  private val getRunPageByWorkspaceId: GetRunPageByWorkspaceId,
  private val getRunByLinkId: GetRunByLinkId,
) {
  @Operation(
    summary = "Retrieves URLs for retrieving/storing cached artifacts",
  )
  @Authenticated
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
  @Authenticated
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
            tasks = dto.toTaskRequests(dto.run.endTime),
            workspaceId = identity.getWorkspaceId(),
          ),
        ) { response ->
          RunSummaryDto.from(response, serverConfiguration.applicationUrl())
        }
      }

  @Operation(
    summary = "Indicates if the workspace for the authentication token is enabled",
  )
  @Authenticated
  @GET
  @Path("/runs/workspace-status")
  @Produces(MediaType.TEXT_PLAIN)
  suspend fun workspaceStatus() = "" // hopefully, all authenticated workspaces are enabled :)

  @Operation(
    summary = "Gets a page of runs for a workspace",
  )
  @GET
  @Path("/private/workspaces/{workspaceId}/runs")
  suspend fun getRunPage(
    workspaceId: String,
    @RestQuery pageIndex: Int,
    @RestQuery pageSize: Int,
  ): PageCollection<RunSimpleDto> =
    getRunPageByWorkspaceId(
      GetRunPageByWorkspaceId.Request(
        workspaceId = WorkspaceId(workspaceId),
        pageIndex = pageIndex,
        pageSize = pageSize,
      ),
    ) { response ->
      PageCollection(
        items = response.runs.items.map { RunSimpleDto.from(it) },
        totalCount = response.runs.totalCount,
      )
    }

  @Operation(
    summary = "Gets a run by its link ID and workspace ID",
  )
  @GET
  @Path("/private/runs/{linkId}")
  suspend fun getRunByLinkId(linkId: String): RunSimpleDto =
    getRunByLinkId(
      GetRunByLinkId.Request(
        linkId = LinkId(linkId),
      ),
    ) { response ->
      RunSimpleDto.from(response.run)
    }

  private suspend fun isReadWriteContext(): Boolean = identity.deferredIdentity.awaitSuspending().hasRole(AccessLevel.READ_WRITE.value)
}
