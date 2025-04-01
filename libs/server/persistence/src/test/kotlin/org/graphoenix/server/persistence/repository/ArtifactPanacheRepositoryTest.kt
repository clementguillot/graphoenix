package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.toContainExactly
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.ArtifactEntity
import org.graphoenix.server.persistence.mapper.toDomain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class ArtifactPanacheRepositoryTest {
  @Inject
  lateinit var artifactPanacheRepository: ArtifactPanacheRepository

  @BeforeEach
  fun setUp() {
    runBlocking {
      artifactPanacheRepository.deleteAll().awaitSuspending()
    }
  }

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
      val expectedArtifacts =
        listOf(
          artifactEntity1.toDomain(),
          artifactEntity2.toDomain(),
          Artifact.Exist(ArtifactId("lol"), Hash("lol"), WorkspaceId("caca"), null, null),
        )

      artifactPanacheRepository.persist(artifactEntity1, artifactEntity2).awaitSuspending()

      // When
      val result = artifactPanacheRepository.findByHash(hashes.map { Hash(it) }, workspaceId)

      // Then
      expect(result).toContainExactly(expectedArtifacts[0], expectedArtifacts[1])
    }

  @Test
  fun `should create new artifacts`() =
    runTest {
      // Given
      val hashes = listOf("hash1", "hash2")
      val workspaceId = WorkspaceId("dummyId")

      // When
      val result = artifactPanacheRepository.createWithHash(hashes.map { Hash(it) }, workspaceId)

      // Then
      expect(result.size).toEqual(2)
      expect(artifactPanacheRepository.count().awaitSuspending()).toEqual(0L)
    }

  @Test
  fun `should create new remote artifacts in the DB`() =
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

      // When
      val result = artifactPanacheRepository.createRemoteArtifacts(artifact, workspaceId)

      // Then
      expect(result).toContainExactly(expectedArtifacts[0], expectedArtifacts[1])
      expect(artifactPanacheRepository.count().awaitSuspending()).toEqual(2L)
    }

  @Test
  fun `should indicate if an artifact has been deleted from the DB`() =
    runTest {
      // Given
      val validArtifact =
        Artifact.Exist(
          id = ArtifactId("valid-id"),
          hash = Hash("hash"),
          workspaceId = WorkspaceId(ObjectId().toString()),
          put = "put",
          get = "get",
        )
      artifactPanacheRepository.createRemoteArtifacts(mapOf(validArtifact.id to validArtifact.hash), validArtifact.workspaceId)
      val invalidArtifact =
        Artifact.Exist(
          id = ArtifactId("invalid-id"),
          hash = Hash("hash"),
          workspaceId = WorkspaceId(ObjectId().toString()),
          put = "put",
          get = "get",
        )

      // When
      val validResult = artifactPanacheRepository.delete(validArtifact.id)
      val invalidResult = artifactPanacheRepository.delete(invalidArtifact.id)

      // Then
      expect(validResult).toEqual(true)
      expect(invalidResult).toEqual(false)
    }
}
