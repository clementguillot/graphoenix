package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.valueobject.*
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

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
          items =
            listOf(
              Run {
                id = RunId("run-id")
                workspaceId = dummyWorkspaceId
                command = "nx test apps/server"
                status = RunStatus.SUCCESS
                startTime = LocalDateTime.now()
                endTime = LocalDateTime.now()
                branch = "main"
                runGroup = "default"
                inner = false
                distributedExecutionId = null
                ciExecutionId = null
                ciExecutionEnv = null
                machineInfo = MachineInfo("machine-id", "linux", "1.0", 4)
                meta = mapOf("nxCloudVersion" to "123")
                vcsContext = null
                linkId = "link-id"
                projectGraph = null
                hashedContributors = null
                sha = "c4a5be0"
              },
            ),
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
