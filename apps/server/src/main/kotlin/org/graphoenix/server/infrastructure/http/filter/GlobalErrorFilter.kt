package org.graphoenix.server.infrastructure.http.filter

import jakarta.annotation.Priority
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger

@Provider
@Priority(9999)
class GlobalErrorFilter : ContainerResponseFilter {
  companion object {
    private val logger: Logger = Logger.getLogger(GlobalErrorFilter::class.java)
  }

  override fun filter(
    requestContext: ContainerRequestContext,
    responseContext: ContainerResponseContext,
  ) {
    if (responseContext.status < 400) {
      return
    }
    logger.info("Error code ${responseContext.status} at ${requestContext.uriInfo.requestUri} - ${responseContext.entity}")
  }
}
