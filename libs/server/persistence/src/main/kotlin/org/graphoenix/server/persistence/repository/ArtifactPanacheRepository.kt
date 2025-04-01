package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.ArtifactEntity
import org.graphoenix.server.persistence.mapper.toDomain
import org.jboss.logging.Logger

@ApplicationScoped
class ArtifactPanacheRepository :
  ReactivePanacheMongoRepository<ArtifactEntity>,
  ArtifactRepository {
  companion object {
    private val logger = Logger.getLogger(ArtifactPanacheRepository::class.java)
  }

  override suspend fun findByHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist> =
    findByHash(hashes.map { it.value }, workspaceId.value)
      .map { it.toDomain() }

  override suspend fun createWithHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.New> =
    hashes.map { hash ->
      Artifact.New(
        id = ArtifactId(),
        workspaceId = workspaceId,
        hash = hash,
        put = null,
      )
    }

  override suspend fun createRemoteArtifacts(
    artifact: Map<ArtifactId, Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist> {
    val entities =
      artifact.map {
        ArtifactEntity(
          id = null,
          artifactId = it.key.value,
          hash = it.value.value,
          workspaceId = ObjectId(workspaceId.value),
        )
      }

    persist(entities).awaitSuspending()

    return entities.map { it.toDomain() }
  }

  override suspend fun delete(artifactId: ArtifactId): Boolean =
    delete(ArtifactEntity::artifactId.name, artifactId.value).awaitSuspending().let {
      when (it) {
        0L -> false
        1L -> true
        else -> {
          logger.warn("$it artifacts were deleted, was expecting only 1, DB indexes should be checked")
          true
        }
      }
    }

  private suspend fun findByHash(
    hashes: Collection<String>,
    workspaceId: String,
  ): List<ArtifactEntity> =
    find(
      "${ArtifactEntity::hash.name} in ?1 and ${ArtifactEntity::workspaceId.name} = ?2",
      hashes,
      ObjectId(workspaceId),
    ).list().awaitSuspending()
}
