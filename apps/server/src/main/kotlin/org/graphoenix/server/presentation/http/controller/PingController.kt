package org.graphoenix.server.presentation.http.controller

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation

@Path("/ping")
class PingController {
  @Operation(
    summary = "Test api",
  )
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  suspend fun ping() = ""
}
