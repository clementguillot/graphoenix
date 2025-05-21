package org.graphoenix.server.presentation.http.mapper

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.application.workspace.exception.OrganizationException
import org.junit.jupiter.api.Test

class OrganizationApplicationExceptionMapperTest {
  private val organizationApplicationExceptionMapper = OrganizationApplicationExceptionMapper()

  @Test
  fun `should return 404 Not Found on OrganizationException NotFound`() {
    // Given
    val dummyException = OrganizationException.NotFound("")

    // When
    val result = organizationApplicationExceptionMapper.mapException(dummyException)

    // Then
    expect(result.status).toEqual(404)
  }
}
