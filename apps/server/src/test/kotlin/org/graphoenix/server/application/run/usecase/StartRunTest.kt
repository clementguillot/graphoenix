package org.graphoenix.server.application.run.usecase

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.gateway.StorageService
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.CheckUnnecessaryStub
class StartRunTest {
  @MockK
  private lateinit var mockArtifactRepository: ArtifactRepository

  @MockK
  private lateinit var mockStorageService: StorageService

  @InjectMockKs
  private lateinit var startRun: StartRun

  @Test
  fun `should return an existing artifact for a known hash`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyHashes = listOf(Hash("hash"))
      val dummyArtifact =
        Artifact.Exist(
          id = ArtifactId(),
          hash = dummyHashes.first(),
          workspaceId = dummyWorkspaceId,
          get = null,
          put = null,
        )
      coEvery { mockArtifactRepository.findByHash(dummyHashes, dummyWorkspaceId) } returns listOf(dummyArtifact)
      coEvery { mockArtifactRepository.createWithHash(emptyList(), dummyWorkspaceId) } returns emptyList()
      coEvery { mockStorageService.generateGetUrl(dummyArtifact.id, dummyWorkspaceId) } returns "test-get-url"
      coEvery { mockStorageService.generatePutUrl(dummyArtifact.id, dummyWorkspaceId) } returns "test-put-url"

      val dummyRequest =
        StartRunRequest(
          hashes = dummyHashes,
          workspaceId = dummyWorkspaceId,
          canPut = true,
        )

      // When
      val result = startRun(dummyRequest) { it }

      // Then
      val firstArtifact = result.artifacts.first()
      expect(firstArtifact).toBeAnInstanceOf<Artifact.Exist>()
      val resultArtifact = firstArtifact as Artifact.Exist
      expect(resultArtifact) {
        its { get }.toEqual("test-get-url")
        its { put }.toEqual("test-put-url")
      }
      coVerify(exactly = 1) { mockArtifactRepository.createWithHash(emptyList(), dummyWorkspaceId) }
    }

  @Test
  fun `should return an new artifact for an unknown hash`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyHashes = listOf(Hash("hash"))
      val dummyArtifact =
        Artifact.New(
          id = ArtifactId(),
          hash = dummyHashes.first(),
          workspaceId = dummyWorkspaceId,
          put = null,
        )
      coEvery { mockArtifactRepository.findByHash(dummyHashes, dummyWorkspaceId) } returns emptyList()
      coEvery { mockArtifactRepository.createWithHash(dummyHashes, dummyWorkspaceId) } returns listOf(dummyArtifact)
      coEvery { mockStorageService.generatePutUrl(dummyArtifact.id, dummyWorkspaceId) } returns "test-put-url"

      val dummyRequest =
        StartRunRequest(
          hashes = dummyHashes,
          workspaceId = dummyWorkspaceId,
          canPut = true,
        )

      // When
      val result = startRun(dummyRequest) { it }

      // Then
      val firstArtifact = result.artifacts.first()
      expect(firstArtifact).toBeAnInstanceOf<Artifact.New>()
      val resultArtifact = result.artifacts.first() as Artifact.New
      expect(resultArtifact.put).toEqual("test-put-url")
    }

  @Test
  fun `should return a read-only artifact if context is not allowed`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId("workspace-id")
      val dummyHashes = listOf(Hash("hash"))
      val dummyArtifact =
        Artifact.Exist(
          id = ArtifactId(),
          hash = dummyHashes.first(),
          workspaceId = dummyWorkspaceId,
          get = null,
          put = null,
        )
      coEvery { mockArtifactRepository.findByHash(dummyHashes, dummyWorkspaceId) } returns listOf(dummyArtifact)
      coEvery { mockArtifactRepository.createWithHash(emptyList(), dummyWorkspaceId) } returns emptyList()
      coEvery { mockStorageService.generateGetUrl(dummyArtifact.id, dummyWorkspaceId) } returns "test-get-url"

      val dummyRequest =
        StartRunRequest(
          hashes = dummyHashes,
          workspaceId = dummyWorkspaceId,
          canPut = false,
        )

      // When
      val result = startRun(dummyRequest) { it }

      // Then
      val firstArtifact = result.artifacts.first()
      expect(firstArtifact).toBeAnInstanceOf<Artifact.Exist>()
      val resultArtifact = result.artifacts.first() as Artifact.Exist
      expect(resultArtifact) {
        its { get }.toEqual("test-get-url")
        its { put }.toEqual(null)
      }
      coVerify(exactly = 1) { mockArtifactRepository.createWithHash(emptyList(), dummyWorkspaceId) }
    }
}
