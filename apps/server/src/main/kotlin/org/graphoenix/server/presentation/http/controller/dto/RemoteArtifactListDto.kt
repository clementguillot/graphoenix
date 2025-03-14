package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.application.run.usecase.StartRunResponse
import org.graphoenix.server.domain.run.entity.Artifact

@RegisterForReflection
data class RemoteArtifactListDto(
  val artifacts: Map<String, RemoteArtifact>,
) {
  companion object {
    fun from(domainResponse: StartRunResponse): RemoteArtifactListDto =
      RemoteArtifactListDto(
        domainResponse.artifacts.associate { artifact ->
          artifact.hash.value to
            RemoteArtifact(
              artifactId = artifact.id.value,
              artifactUrls =
                RemoteArtifact.Url.from(artifact),
            )
        },
      )
  }

  data class RemoteArtifact(
    val artifactId: String,
    val artifactUrls: Url,
  ) {
    data class Url(
      val get: String?,
      val put: String?,
    ) {
      companion object {
        fun from(artifact: Artifact) =
          when (artifact) {
            is Artifact.Exist -> Url(artifact.get, artifact.put)
            is Artifact.New -> Url(null, artifact.put)
          }
      }
    }
  }
}
