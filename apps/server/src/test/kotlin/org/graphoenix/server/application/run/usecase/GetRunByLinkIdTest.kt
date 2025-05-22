package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.application.run.exception.RunException
import org.graphoenix.server.buildRun
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class GetRunByLinkIdTest {
  @MockK
  private lateinit var mockRunRepository: RunRepository

  @InjectMockKs
  private lateinit var getRunByLinkId: GetRunByLinkId

  @Test
  fun `should get an existing run by its ID`() =
    runTest {
      // Given
      val dummyRunLinkId = LinkId("link-id")
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      coEvery { mockRunRepository.findByLinkId(dummyRunLinkId) } returns
        buildRun(
          workspaceId = dummyWorkspaceId,
          linkId = dummyRunLinkId,
        )

      // When
      val result = getRunByLinkId(GetRunByLinkId.Request(dummyRunLinkId)) { it.run }

      // Then
      expect(result.linkId).toEqual(dummyRunLinkId)
    }

  @Test
  fun `should throw an exception on run not found`() =
    runTest {
      // Given
      coEvery { mockRunRepository.findByLinkId(any()) } returns null

      // When & Then
      expect {
        runBlocking {
          getRunByLinkId(GetRunByLinkId.Request(LinkId("not-found"))) { }
        }
      }.toThrow<RunException.NotFound>()
    }
}
