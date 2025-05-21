package org.graphoenix.server.presentation.http.mapper

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.junit.jupiter.api.Test

@QuarkusTest
class WorkspaceApplicationExceptionMapperTest {
  private var workspaceApplicationExceptionMapper = WorkspaceApplicationExceptionMapper()

  @Test
  fun `should return 404 Not Found on WorkspaceException NotFound`() {
    // Given
    val dummyException = WorkspaceException.NotFound("")

    // When
    val result = workspaceApplicationExceptionMapper.mapException(dummyException)

    // Then
    expect(result.status).toEqual(404)
  }

  @Test
  fun `should return 400 Bad Request on WorkspaceException OrganizationNotFound`() {
    // Given
    val dummyException = WorkspaceException.OrganizationNotFound("")

    // When
    val result = workspaceApplicationExceptionMapper.mapException(dummyException)

    // Then
    expect(result.status).toEqual(400)
  }
}
