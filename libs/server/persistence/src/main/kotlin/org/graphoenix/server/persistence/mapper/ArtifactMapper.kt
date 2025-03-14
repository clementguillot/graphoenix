package org.graphoenix.server.persistence.mapper

import org.graphoenix.server.domain.run.entity.Artifact
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.ArtifactEntity

fun ArtifactEntity.toDomain() =
  Artifact.Exist(
    id = ArtifactId(artifactId),
    hash = Hash(hash),
    workspaceId = WorkspaceId(workspaceId.toString()),
    get = null,
    put = null,
  )
