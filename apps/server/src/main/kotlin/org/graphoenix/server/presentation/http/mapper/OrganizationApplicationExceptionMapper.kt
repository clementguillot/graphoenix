package org.graphoenix.server.presentation.http.mapper

import jakarta.ws.rs.core.Response
import org.graphoenix.server.application.workspace.exception.OrganizationException
import org.jboss.resteasy.reactive.server.ServerExceptionMapper

class OrganizationApplicationExceptionMapper {
  @ServerExceptionMapper
  fun mapException(exception: OrganizationException): Response =
    when (exception) {
      is OrganizationException.NotFound -> Response.status(Response.Status.NOT_FOUND).build()
    }
}
