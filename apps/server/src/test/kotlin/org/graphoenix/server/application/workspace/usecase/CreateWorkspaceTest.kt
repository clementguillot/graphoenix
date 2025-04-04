package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.application.workspace.exception.OrganizationNotFoundException
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test

class CreateWorkspaceTest {
  private val mockWorkspaceRepository = mockk<WorkspaceRepository>()
  private val mockOrganizationRepository = mockk<OrganizationRepository>()
  private val createWorkspace = CreateWorkspace(mockWorkspaceRepository, mockOrganizationRepository)

  @Test
  fun `should throw if request Org ID is not found`() =
    runTest {
      // Given
      val dummyOrgId = OrganizationId("not-found-id")
      val dummyRequest = CreateWorkspaceRequest(orgId = dummyOrgId, name = "fail workspace")

      coEvery { mockOrganizationRepository.isValidOrgId(dummyOrgId) } returns false

      // When and then
      expect {
        runBlocking { createWorkspace(dummyRequest) {} }
      }.toThrow<OrganizationNotFoundException>()
    }

  @Test
  fun `should return the newly created workspace`() =
    runTest {
      // Given
      val dummyOrgId = OrganizationId("valid-org-id")
      val dummyWorkspaceName = "new workspace"
      val dummyWorkspace =
        Workspace(
          id = WorkspaceId("123"),
          orgId = dummyOrgId,
          name = dummyWorkspaceName,
          installationSource = null,
          nxInitDate = null,
        )
      val dummyRequest = CreateWorkspaceRequest(orgId = dummyOrgId, name = dummyWorkspaceName)
      val dummyResponse = CreateWorkspaceResponse(dummyWorkspace)

      coEvery { mockOrganizationRepository.isValidOrgId(dummyOrgId) } returns true
      coEvery { mockWorkspaceRepository.create(dummyRequest.name, dummyRequest.orgId) } returns dummyWorkspace

      // When
      val result = createWorkspace(dummyRequest) { it }

      // Then
      expect(result).toEqual(dummyResponse)
      coVerify(exactly = 1) { mockWorkspaceRepository.create(dummyRequest.name, dummyRequest.orgId) }
    }
}
