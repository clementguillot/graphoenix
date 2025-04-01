package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.Test

class CreateOrganizationTest {
  private val mockOrgRepository = mockk<OrganizationRepository>()
  private val createOrganization = CreateOrganization(mockOrgRepository)

  @Test
  fun `should return the newly created organization`() =
    runTest {
      // Given
      val dummyOrgName = "new org"
      val dummyOrg = Organization(OrganizationId("123"), dummyOrgName)
      val dummyRequest = CreateOrganizationRequest(name = dummyOrgName)
      val dummyResponse = CreateOrganizationResponse(dummyOrg)

      coEvery { mockOrgRepository.create(dummyOrgName) } returns dummyOrg

      // When
      val result = createOrganization(dummyRequest) { it }

      // Then
      expect(result).toEqual(dummyResponse)
      coVerify(exactly = 1) { mockOrgRepository.create(dummyOrgName) }
    }
}
