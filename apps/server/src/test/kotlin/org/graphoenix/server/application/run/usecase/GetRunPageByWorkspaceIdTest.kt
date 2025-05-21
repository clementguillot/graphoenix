package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.buildRun
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetRunPageByWorkspaceIdTest {
  @MockK
  private lateinit var runRepository: RunRepository

  @InjectMockKs
  private lateinit var getRunPageByWorkspaceId: GetRunPageByWorkspaceId

  @Test
  fun `should get a page of runs by their workspace ID`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyRunPage =
        PageCollection(
          items = listOf(buildRun(dummyWorkspaceId)),
          totalCount = 1,
        )
      coEvery { runRepository.findPageByWorkspaceId(dummyWorkspaceId, 0, 10) } returns dummyRunPage

      // When
      val result =
        getRunPageByWorkspaceId(
          GetRunPageByWorkspaceId.Request(
            workspaceId = dummyWorkspaceId,
            pageIndex = 0,
            pageSize = 10,
          ),
        ) { it.runs }

      // Then
      expect(result.items).toContainExactlyElementsOf(dummyRunPage.items)
    }
}
