package org.graphoenix.server.domain.run.gateway

import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

interface StorageService {
  suspend fun generateGetUrl(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  ): String

  suspend fun generatePutUrl(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  ): String

  suspend fun deleteArtifact(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  )
}
