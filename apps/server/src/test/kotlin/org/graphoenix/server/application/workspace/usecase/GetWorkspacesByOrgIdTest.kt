package org.graphoenix.server.application.workspace.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetWorkspacesByOrgIdTest {
  @MockK
  private lateinit var workspaceRepository: WorkspaceRepository

  @InjectMockKs
  private lateinit var getWorkspacesByOrgId: GetWorkspacesByOrgId

  @Test
  fun `should get workspaces by their org ID`() =
    runTest {
      // Given
      val dummyOrgId = OrganizationId("org-id")
      val dummyWorkspaces =
        listOf(
          Workspace(
            id = WorkspaceId("workspace-id"),
            orgId = dummyOrgId,
            name = "workspace",
            installationSource = "~/",
            nxInitDate = LocalDateTime.now(),
          ),
        )
      coEvery { workspaceRepository.findAllByOrgId(dummyOrgId) } returns dummyWorkspaces

      // When
      val result = getWorkspacesByOrgId(GetWorkspacesByOrgId.Request(dummyOrgId)) { it.workspaces }

      // Then
      expect(result).toContainExactlyElementsOf(dummyWorkspaces)
    }
}
