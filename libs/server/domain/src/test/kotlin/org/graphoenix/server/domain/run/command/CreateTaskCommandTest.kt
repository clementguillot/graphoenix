package org.graphoenix.server.domain.run.command

import ch.tutteli.atrium.api.fluent.en_GB.toBeAnInstanceOf
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.run.valueobject.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CreateTaskCommandTest {
  @Test
  fun `should instantiate`() {
    expect(
      CreateTaskCommand(
        taskId = "taskId",
        hash = Hash("hash"),
        projectName = "projectName",
        target = "target",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now(),
        cacheStatus = CacheStatus.CACHE_MISS,
        status = 1,
        uploadedToStorage = true,
        terminalOutputUploadedToFileStorage = true,
        isCacheable = true,
        parallelism = true,
        params = "params",
        terminalOutput = "terminalOutput",
        hashDetails =
          HashDetails(
            implicitDeps = emptyMap(),
            nodes = emptyMap(),
            runtime = emptyMap(),
          ),
        artifactId = ArtifactId("artifactId"),
        meta = null,
      ),
    ).toBeAnInstanceOf<CreateTaskCommand>()
  }
}
