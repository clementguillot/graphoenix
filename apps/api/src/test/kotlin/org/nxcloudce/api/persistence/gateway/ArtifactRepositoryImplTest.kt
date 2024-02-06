package org.nxcloudce.api.persistence.gateway

import io.mockk.coEvery
import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.nxcloudce.api.domain.run.model.Artifact
import org.nxcloudce.api.domain.run.model.ArtifactId
import org.nxcloudce.api.domain.run.model.Hash
import org.nxcloudce.api.domain.workspace.model.WorkspaceId
import org.nxcloudce.api.persistence.entity.ArtifactEntity
import org.nxcloudce.api.persistence.repository.ArtifactPanacheRepository
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@QuarkusTest
class ArtifactRepositoryImplTest {
  @InjectMock
  lateinit var mockArtifactPanacheRepository: ArtifactPanacheRepository

  @Inject
  lateinit var artifactRepository: ArtifactRepositoryImpl

  @Test
  fun `should find existing artifacts by their hashes`() =
    runTest {
      // Given
      val hashes = listOf("hash1", "hash2")
      val workspaceId = WorkspaceId(ObjectId().toString())
      val artifactEntity1 =
        ArtifactEntity(ObjectId(), "artifactId1", "hash1", ObjectId(workspaceId.value))
      val artifactEntity2 =
        ArtifactEntity(ObjectId(), "artifactId2", "hash2", ObjectId(workspaceId.value))
      val expectedArtifacts = listOf(artifactEntity1.toDomain(), artifactEntity2.toDomain())

      coEvery {
        mockArtifactPanacheRepository.findByHash(
          hashes,
          workspaceId,
        )
      } returns listOf(artifactEntity1, artifactEntity2)

      // When
      val result = artifactRepository.findByHash(hashes.map { Hash(it) }, workspaceId)

      // Then
      assertContentEquals(expectedArtifacts, result)
    }

  @Test
  fun `should create new artifacts`() =
    runTest {
      // Given
      val hashes = listOf("hash1", "hash2")
      val workspaceId = WorkspaceId("dummyId")

      // When
      val result = artifactRepository.createWithHash(hashes.map { Hash(it) }, workspaceId)

      // Then
      assertEquals(2, result.size)
    }

  @Test
  fun `should create new remote artifacts`() =
    runTest {
      // Given
      val artifact =
        mapOf(
          ArtifactId("artifactId1") to Hash("hash1"),
          ArtifactId("artifactId2") to Hash("hash2"),
        )
      val workspaceId = WorkspaceId(ObjectId().toString())
      val expectedArtifacts =
        listOf(
          Artifact.Exist(
            id = ArtifactId("artifactId1"),
            hash = Hash("hash1"),
            workspaceId = workspaceId,
            put = null,
            get = null,
          ),
          Artifact.Exist(
            id = ArtifactId("artifactId2"),
            hash = Hash("hash2"),
            workspaceId = workspaceId,
            put = null,
            get = null,
          ),
        )
      every {
        mockArtifactPanacheRepository.persist(any<Collection<ArtifactEntity>>())
      } answers {
        (firstArg<Collection<ArtifactEntity>>()).forEach { entity ->
          entity.id = ObjectId()
        }
        Uni.createFrom().voidItem()
      }

      // When
      val result = artifactRepository.createRemoteArtifacts(artifact, workspaceId)

      // Then
      assertContentEquals(expectedArtifacts, result)
    }
}