package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class WorkspacePanacheRepositoryTest {
  @Inject
  lateinit var workspacePanacheRepository: WorkspacePanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      workspacePanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should create a new workspace in the DB`() =
    runTest {
      // Given
      val dummyEntityId = ObjectId()
      val organizationId = OrganizationId(ObjectId().toString())
      val workspaceName = "test org"

      // When
      val result = workspacePanacheRepository.create(workspaceName, organizationId)

      // Then
      expect(result) {
        its { name }.toEqual(workspaceName)
      }
      expect(workspacePanacheRepository.count().awaitSuspending()).toEqual(1L)
    }
}
