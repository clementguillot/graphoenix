package org.graphoenix.server.presentation.http.mapper

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.application.run.exception.TaskException
import org.junit.jupiter.api.Test

class TaskApplicationExceptionMapperTest {
  private val runApplicationExceptionMapper = TaskApplicationExceptionMapper()

  @Test
  fun `should return 404 Not Found on TaskException RunNotFound`() {
    // Given
    val dummyException = TaskException.RunNotFound("")

    // When
    val result = runApplicationExceptionMapper.mapException(dummyException)

    // Then
    expect(result.status).toEqual(404)
  }
}
