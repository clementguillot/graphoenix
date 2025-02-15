package org.graphoenix.server.technical

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.graphoenix.server.domain.workspace.exception.OrganizationNotFoundException

@Provider
class DomainExceptionMapper : ExceptionMapper<OrganizationNotFoundException> {
  override fun toResponse(exception: OrganizationNotFoundException): Response = Response.status(Response.Status.BAD_REQUEST).build()
}
