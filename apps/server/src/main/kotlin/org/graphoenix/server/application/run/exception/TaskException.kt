package org.graphoenix.server.application.run.exception

sealed class TaskException : RuntimeException() {
  data class RunNotFound(
    override val message: String,
  ) : TaskException()
}
