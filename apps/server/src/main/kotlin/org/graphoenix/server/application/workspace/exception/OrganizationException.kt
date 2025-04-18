package org.graphoenix.server.application.workspace.exception

sealed class OrganizationException : RuntimeException() {
  data class NotFound(
    override val message: String,
  ) : OrganizationException()
}
