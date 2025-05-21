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
import org.graphoenix.server.application.workspace.exception.WorkspaceException
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetWorkspaceByIdTest {
  @MockK
  private lateinit var workspaceRepository: WorkspaceRepository

  @InjectMockKs
  private lateinit var getWorkspaceById: GetWorkspaceById

  @Test
  fun `should get an existing workspace by its ID`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyOrganizationId = OrganizationId("org-id")
      coEvery { workspaceRepository.findByIdAndOrgId(dummyWorkspaceId, dummyOrganizationId) } returns
        Workspace(
          id = dummyWorkspaceId,
          name = "dummy workspace",
          orgId = dummyOrganizationId,
          nxInitDate = LocalDateTime.now(),
          installationSource = null,
        )

      // When
      val result = getWorkspaceById(GetWorkspaceById.Request(dummyWorkspaceId, dummyOrganizationId)) { it.workspace }

      // Then
      expect(result.id).toEqual(dummyWorkspaceId)
    }

  @Test
  fun `should throw an exception on workspace not found`() =
    runTest {
      // Given
      coEvery { workspaceRepository.findByIdAndOrgId(any(), any()) } returns null

      // When & Then
      expect {
        runBlocking {
          getWorkspaceById(GetWorkspaceById.Request(WorkspaceId("workspace-id"), OrganizationId("org-id"))) { }
        }
      }.toThrow<WorkspaceException.NotFound>()
    }
}
