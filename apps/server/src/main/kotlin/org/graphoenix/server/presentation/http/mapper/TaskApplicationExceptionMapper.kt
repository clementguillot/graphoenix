package org.graphoenix.server.presentation.http.mapper

import jakarta.ws.rs.core.Response
import org.graphoenix.server.application.run.exception.TaskException
import org.jboss.resteasy.reactive.server.ServerExceptionMapper

class TaskApplicationExceptionMapper {
  @ServerExceptionMapper
  fun mapException(exception: TaskException): Response =
    when (exception) {
      is TaskException.RunNotFound -> Response.status(Response.Status.NOT_FOUND).build()
    }
}
