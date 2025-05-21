package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetAllOrganizationsTest {
  @MockK
  private lateinit var organizationRepository: OrganizationRepository

  @InjectMockKs
  private lateinit var getAllOrganizations: GetAllOrganizations

  @Test
  fun `should get all organizations`() =
    runTest {
      // Given
      val dummyOrgs = listOf(Organization(id = OrganizationId("id-1"), name = "org-1"))
      coEvery { organizationRepository.findAllOrgs() } returns dummyOrgs

      // When
      val result = getAllOrganizations(Unit) { it.organizations }

      // Then
      expect(result).toContainExactlyElementsOf(dummyOrgs)
    }
}
