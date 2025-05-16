package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.entity.*
import org.graphoenix.server.domain.workspace.gateway.*
import org.graphoenix.server.domain.workspace.valueobject.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@QuarkusTest
class CreateOrgAndWorkspaceTest {
  private val mockWorkspaceRepository = mockk<WorkspaceRepository>()
  private val mockOrganizationRepository = mockk<OrganizationRepository>()
  private val mockAccessTokenRepository = mockk<AccessTokenRepository>()
  private val createOrgAndWorkspace = CreateOrgAndWorkspace(mockWorkspaceRepository, mockOrganizationRepository, mockAccessTokenRepository)

  @Test
  fun `should create org with same name as workspace, workspace and default access token`() =
    runTest {
      // Given
      val dummyOrgId = OrganizationId("org-id")
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyWorkspaceName = "test workspace"
      val dummyRequest =
        CreateOrgAndWorkspaceRequest(workspaceName = dummyWorkspaceName, installationSource = "junit", nxInitDate = LocalDateTime.now())

      coEvery { mockOrganizationRepository.create(dummyWorkspaceName) } returns Organization(dummyOrgId, dummyWorkspaceName)
      coEvery {
        mockWorkspaceRepository.create(dummyRequest.workspaceName, dummyRequest.installationSource, dummyRequest.nxInitDate, dummyOrgId)
      } returns
        Workspace(dummyWorkspaceId, dummyOrgId, dummyWorkspaceName, "junit", null)
      coEvery { mockAccessTokenRepository.createDefaultAccessToken(dummyWorkspaceId) } returns
        AccessToken {
          id = AccessTokenId("token-ID")
          workspaceId = dummyWorkspaceId
          accessLevel = AccessLevel.READ_WRITE
          name = "default"
          publicId = AccessTokenPublicId()
          encodedValue = "base64content"
        }

      // When
      val response = createOrgAndWorkspace(dummyRequest) { it }

      // Then
      expect(response) {
        its { workspace.name }.toEqual(dummyWorkspaceName)
        its { workspace.id }.toEqual(dummyWorkspaceId)
        its { accessToken.name }.toEqual("default")
        its { accessToken.accessLevel }.toEqual(AccessLevel.READ_WRITE)
      }
    }
}
