package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.gateway.ArtifactRepository
import org.graphoenix.server.domain.run.gateway.StorageService
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

@ApplicationScoped
class StartRun(
  private val artifactRepository: ArtifactRepository,
  private val storageService: StorageService,
) : UseCase<StartRunRequest, StartRunResponse> {
  override suspend operator fun <T> invoke(
    request: StartRunRequest,
    presenter: (StartRunResponse) -> T,
  ): T {
    val existingArtifacts = artifactRepository.findByHash(request.hashes, request.workspaceId)
    val existingHashes = existingArtifacts.map { it.hash }
    val newArtifacts =
      artifactRepository.createWithHash(
        request.hashes.filter { it !in existingHashes },
        request.workspaceId,
      )
    val artifacts = existingArtifacts + newArtifacts

    coroutineScope {
      artifacts.map { artifact ->
        async {
          updateArtifactUrls(artifact, request.canPut)
        }
      }
    }.awaitAll()

    return presenter(StartRunResponse(artifacts = artifacts))
  }

  private suspend fun updateArtifactUrls(
    artifact: Artifact,
    canPut: Boolean,
  ) {
    if (artifact is Artifact.Exist) {
      artifact.setGetUrl(storageService)
    }
    if (canPut) {
      artifact.setPutUrl(storageService)
    }
  }
}

data class StartRunRequest(
  val hashes: Collection<Hash>,
  val workspaceId: WorkspaceId,
  val canPut: Boolean,
)

data class StartRunResponse(
  val artifacts: Collection<Artifact>,
)
