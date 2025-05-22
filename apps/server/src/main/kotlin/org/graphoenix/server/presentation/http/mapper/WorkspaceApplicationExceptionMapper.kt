package org.graphoenix.server.presentation.http.mapper

import jakarta.ws.rs.core.Response
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.jboss.resteasy.reactive.server.ServerExceptionMapper

class WorkspaceApplicationExceptionMapper {
  @ServerExceptionMapper
  fun mapException(exception: WorkspaceException): Response =
    when (exception) {
      is WorkspaceException.NotFound -> Response.status(Response.Status.NOT_FOUND).build()
      is WorkspaceException.OrganizationNotFound -> Response.status(Response.Status.BAD_REQUEST).build()
    }
}
