package org.graphoenix.server.presentation.http.controller

import io.quarkus.security.Authenticated
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.infrastructure.io.GzipJsonDecoder
import org.graphoenix.server.presentation.http.controller.dto.HeartbeatDto
import org.jboss.logging.Logger

@Path("/heartbeat")
@Authenticated
class HeartbeatController(
  private val gzipJsonDecoder: GzipJsonDecoder,
) {
  companion object {
    private val logger = Logger.getLogger(HeartbeatController::class.java)
  }

  @Operation(summary = "Receives a heartbeat from a run group")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  suspend fun receive(heartbeatDto: HeartbeatDto): Response {
    logger.info("Received heartbeat from run group '${heartbeatDto.runGroup}' (CI ID: '${heartbeatDto.ciExecutionId}')")
    return Response.ok().build()
  }

  @Operation(summary = "Receives a heartbeat logs from a run group")
  @POST
  @Path("/logs")
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  suspend fun receiveLog(request: ByteArray): Response {
    val dto = gzipJsonDecoder.from(request, HeartbeatDto::class)
    logger.info("Received heartbeat from run group '${dto.runGroup}' (CI ID: '${dto.ciExecutionId}'). Logs: \n${dto.logs}")
    return Response.ok().build()
  }
}
