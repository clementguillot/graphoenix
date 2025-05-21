package org.graphoenix.server.presentation.http.mapper

import jakarta.ws.rs.core.Response
import org.graphoenix.server.application.run.exception.RunException
import org.jboss.resteasy.reactive.server.ServerExceptionMapper

class RunApplicationExceptionMapper {
  @ServerExceptionMapper
  fun mapException(exception: RunException): Response =
    when (exception) {
      is RunException.NotFound -> Response.status(Response.Status.NOT_FOUND).build()
    }
}
