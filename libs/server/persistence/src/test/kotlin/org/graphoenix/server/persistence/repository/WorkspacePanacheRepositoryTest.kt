package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
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

  @Test
  fun `should find all workspaces by organization ID`() =
    runTest {
      // Given
      val dummyOrganizationId = OrganizationId(ObjectId().toString())
      val workspaces =
        listOf(
          workspacePanacheRepository.create("workspace A", dummyOrganizationId),
          workspacePanacheRepository.create("workspace B", dummyOrganizationId),
        )
      workspacePanacheRepository.create("workspace C", OrganizationId(ObjectId().toString()))

      // When
      val result = workspacePanacheRepository.findAllByOrgId(dummyOrganizationId)

      // Then
      expect(workspacePanacheRepository.count().awaitSuspending()).toEqual(3L)
      expect(result) {
        its { size }.toEqual(2)
        its { map { it.id } }.toContainExactlyElementsOf(workspaces.map { it.id })
      }
    }

  @Test
  fun `should find a workspace by its ID and organization ID`() =
    runTest {
      // Given
      val dummyWorkspace = workspacePanacheRepository.create("workspace", OrganizationId(ObjectId().toString()))

      // When
      val resultFound = workspacePanacheRepository.findByIdAndOrgId(dummyWorkspace.id, dummyWorkspace.orgId)
      val resultNotFound =
        workspacePanacheRepository.findByIdAndOrgId(
          WorkspaceId(ObjectId().toString()),
          OrganizationId(ObjectId().toString()),
        )

      // Then
      expect(resultFound).toEqual(dummyWorkspace)
      expect(resultNotFound).toEqual(null)
    }
}
