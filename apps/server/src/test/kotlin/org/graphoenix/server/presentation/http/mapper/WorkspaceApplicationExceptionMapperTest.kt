package org.graphoenix.server.presentation.http.mapper

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.graphoenix.server.application.workspace.exception.OrganizationNotFoundException
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.Test

@QuarkusTest
class WorkspaceApplicationExceptionMapperTest {
  @Inject
  lateinit var workspaceApplicationExceptionMapper: WorkspaceApplicationExceptionMapper

  @Test
  fun `should return 400 Bad Request on OrganizationNotFoundException`() {
    val dummyException = OrganizationNotFoundException(OrganizationId("dummy-id"))

    val result = workspaceApplicationExceptionMapper.toResponse(dummyException)

    expect(result.status).toEqual(400)
  }
}
