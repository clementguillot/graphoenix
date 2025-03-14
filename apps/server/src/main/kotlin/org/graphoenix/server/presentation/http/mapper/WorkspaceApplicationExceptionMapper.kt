package org.graphoenix.server.presentation.http.mapper

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.graphoenix.server.application.workspace.exception.OrganizationNotFoundException

@Provider
class WorkspaceApplicationExceptionMapper : ExceptionMapper<OrganizationNotFoundException> {
  override fun toResponse(exception: OrganizationNotFoundException): Response = Response.status(Response.Status.BAD_REQUEST).build()
}
