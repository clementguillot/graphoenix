package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.application.workspace.exception.OrganizationException
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetOrganizationByIdTest {
  @MockK
  private lateinit var organizationRepository: OrganizationRepository

  @InjectMockKs
  private lateinit var getOrganizationById: GetOrganizationById

  @Test
  fun `should get an existing organization by its ID`() =
    runTest {
      // Given
      val dummyOrganizationId = OrganizationId("org-id")
      coEvery { organizationRepository.findById(dummyOrganizationId) } returns
        Organization(
          id = dummyOrganizationId,
          name = "dummy org",
        )

      // When
      val result = getOrganizationById(GetOrganizationById.Request(dummyOrganizationId)) { it.organization }

      // Then
      expect(result.id).toEqual(dummyOrganizationId)
    }

  @Test
  fun `should throw an exception on organization not found`() =
    runTest {
      // Given
      val dummyOrganizationId = OrganizationId("org-id")
      coEvery { organizationRepository.findById(dummyOrganizationId) } returns null

      // When & Then
      expect {
        runBlocking {
          getOrganizationById(GetOrganizationById.Request(dummyOrganizationId)) { }
        }
      }.toThrow<OrganizationException.NotFound>()
    }
}
