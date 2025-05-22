package org.graphoenix.server.application.workspace.exception

sealed class WorkspaceException : RuntimeException() {
  data class NotFound(
    override val message: String,
  ) : WorkspaceException()

  data class OrganizationNotFound(
    override val message: String,
  ) : WorkspaceException()
}
