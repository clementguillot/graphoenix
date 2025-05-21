package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.application.run.exception.TaskException
import org.graphoenix.server.buildRun
import org.graphoenix.server.buildTask
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.gateway.TaskRepository
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetTaskPageByRunTest {
  @MockK
  private lateinit var mockRunRepository: RunRepository

  @MockK
  private lateinit var mockTaskRepository: TaskRepository

  @InjectMockKs
  private lateinit var getTaskPageByRun: GetTaskPageByRun

  @Test
  fun `should get a page of tasks by their run and workspace ID`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyRunLinkId = LinkId("run-link-id")
      val dummyRun = buildRun(workspaceId = dummyWorkspaceId, linkId = dummyRunLinkId)
      val dummyTaskPage = PageCollection(items = listOf(buildTask(runId = dummyRun.id, workspaceId = dummyWorkspaceId)), totalCount = 1)

      coEvery { mockRunRepository.findByLinkId(dummyRunLinkId) } returns dummyRun
      coEvery { mockTaskRepository.findPageByRunIdAndWorkspaceId(dummyRun.id, dummyWorkspaceId, 0, 10) } returns dummyTaskPage

      // When
      val result = getTaskPageByRun(GetTaskPageByRun.Request(dummyRunLinkId, dummyWorkspaceId, 0, 10)) { it.tasks }

      // Then
      expect(result.items).toContainExactlyElementsOf(dummyTaskPage.items)
    }

  @Test
  fun `should throw an exception on run not found`() =
    runTest {
      // Given
      coEvery { mockRunRepository.findByLinkId(any()) } returns null

      // When & Then
      expect {
        runBlocking {
          getTaskPageByRun(GetTaskPageByRun.Request(LinkId("broken-link"), WorkspaceId("workspace-id"), 0, 10)) { }
        }
      }.toThrow<TaskException.RunNotFound>()
    }
}
