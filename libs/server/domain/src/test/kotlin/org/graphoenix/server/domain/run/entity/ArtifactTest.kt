package org.graphoenix.server.domain.run.entity

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.run.valueobject.Hash
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test

class ArtifactTest {
  @Test
  fun `should build a new instance of Artifact`() {
    val existingArtifact =
      Artifact.Exist(
        id = ArtifactId("artifact-id"),
        hash = Hash("hash"),
        workspaceId = WorkspaceId("workspace-id"),
        put = "put",
        get = "get",
      )

    expect(existingArtifact) {
      its { id.value }.toEqual("artifact-id")
      its { hash.value }.toEqual("hash")
      its { workspaceId.value }.toEqual("workspace-id")
      its { put }.toEqual("put")
      its { get }.toEqual("get")
    }

    val newArtifact =
      Artifact.New(
        id = ArtifactId("artifact-id"),
        hash = Hash("hash"),
        workspaceId = WorkspaceId("workspace-id"),
        put = "put",
      )

    expect(newArtifact) {
      its { id.value }.toEqual("artifact-id")
      its { hash.value }.toEqual("hash")
      its { workspaceId.value }.toEqual("workspace-id")
      its { put }.toEqual("put")
    }
  }
}
