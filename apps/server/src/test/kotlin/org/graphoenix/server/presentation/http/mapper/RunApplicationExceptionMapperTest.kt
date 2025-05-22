package org.graphoenix.server.presentation.http.mapper

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.application.run.exception.RunException
import org.junit.jupiter.api.Test

class RunApplicationExceptionMapperTest {
  private val runApplicationExceptionMapper = RunApplicationExceptionMapper()

  @Test
  fun `should return 404 Not Found on RunException NotFound`() {
    // Given
    val dummyException = RunException.NotFound("")

    // When
    val result = runApplicationExceptionMapper.mapException(dummyException)

    // Then
    expect(result.status).toEqual(404)
  }
}
