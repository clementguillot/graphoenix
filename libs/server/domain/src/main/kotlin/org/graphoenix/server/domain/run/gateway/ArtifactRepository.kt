package org.graphoenix.server.domain.run.gateway

import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

interface ArtifactRepository {
  suspend fun findByHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist>

  suspend fun createWithHash(
    hashes: Collection<Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.New>

  suspend fun createRemoteArtifacts(
    artifact: Map<ArtifactId, Hash>,
    workspaceId: WorkspaceId,
  ): Collection<Artifact.Exist>

  suspend fun delete(artifactId: ArtifactId): Boolean
}
