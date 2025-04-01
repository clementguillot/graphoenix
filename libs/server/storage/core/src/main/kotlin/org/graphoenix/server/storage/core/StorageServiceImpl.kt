package org.graphoenix.server.storage.core

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import org.graphoenix.server.domain.run.gateway.StorageService
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class StorageServiceImpl(
  private val fileRepository: Instance<FileRepository>,
) : StorageService {
  override suspend fun generateGetUrl(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  ): String = fileRepository.get().generateGetUrl("${workspaceId.value}/${artifactId.value}")

  override suspend fun generatePutUrl(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  ): String = fileRepository.get().generatePutUrl("${workspaceId.value}/${artifactId.value}")

  override suspend fun deleteArtifact(
    artifactId: ArtifactId,
    workspaceId: WorkspaceId,
  ) = fileRepository.get().deleteFile("${workspaceId.value}/${artifactId.value}")
}
