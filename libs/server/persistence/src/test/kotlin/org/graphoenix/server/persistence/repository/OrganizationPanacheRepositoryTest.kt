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
import org.graphoenix.server.persistence.entity.OrganizationEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class OrganizationPanacheRepositoryTest {
  @Inject
  lateinit var organizationPanacheRepository: OrganizationPanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      organizationPanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should create a new organization in the DB `() =
    runTest {
      // Given
      val dummyRequest = "test"

      // When
      val result = organizationPanacheRepository.create(dummyRequest)

      // Then
      expect(result.name).toEqual(dummyRequest)
      expect(organizationPanacheRepository.count().awaitSuspending()).toEqual(1L)
    }

  @Test
  fun `should indicate if an org ID is valid or not`() =
    runTest {
      // Given
      val validId = ObjectId()
      val dummyEntity = OrganizationEntity(id = validId, name = "my org")
      organizationPanacheRepository.persist(dummyEntity).awaitSuspending()

      // When
      val existingId = organizationPanacheRepository.isValidOrgId(OrganizationId(validId.toString()))
      val invalidId = organizationPanacheRepository.isValidOrgId(OrganizationId(ObjectId().toString()))

      // Then
      expect(existingId).toEqual(true)
      expect(invalidId).toEqual(false)
    }

  @Test
  fun `should find an organization by its ID`() =
    runTest {
      // Given
      val dummyOrganization = organizationPanacheRepository.create("organization")

      // When
      val resultFound = organizationPanacheRepository.findById(dummyOrganization.id)
      val resultNotFound = organizationPanacheRepository.findById(OrganizationId(ObjectId().toString()))

      // Then
      expect(resultFound).toEqual(dummyOrganization)
      expect(resultNotFound).toEqual(null)
    }

  @Test
  fun `should find all organizations`() =
    runTest {
      // Given
      val dummyOrgs =
        listOf(
          organizationPanacheRepository.create("organization A"),
          organizationPanacheRepository.create("organization B"),
          organizationPanacheRepository.create("organization C"),
        )

      // When
      val result = organizationPanacheRepository.findAllOrgs()

      // Then
      expect(result) {
        its { size }.toEqual(3)
        toContainExactlyElementsOf(dummyOrgs)
      }
    }
}
