package org.graphoenix.server.application.run.exception

sealed class RunException : RuntimeException() {
  data class NotFound(
    override val message: String,
  ) : RunException()
}
