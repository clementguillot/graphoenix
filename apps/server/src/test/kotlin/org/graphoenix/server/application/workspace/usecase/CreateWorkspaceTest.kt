package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class CreateWorkspaceTest {
  @MockK
  private lateinit var mockWorkspaceRepository: WorkspaceRepository

  @MockK
  private lateinit var mockOrganizationRepository: OrganizationRepository

  @InjectMockKs
  private lateinit var createWorkspace: CreateWorkspace

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
      }.toThrow<WorkspaceException.OrganizationNotFound>()
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
