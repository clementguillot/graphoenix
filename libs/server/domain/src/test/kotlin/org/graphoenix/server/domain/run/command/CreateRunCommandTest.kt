package org.graphoenix.server.domain.run.command

import ch.tutteli.atrium.api.fluent.en_GB.toBeAnInstanceOf
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.run.valueobject.MachineInfo
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CreateRunCommandTest {
  @Test
  fun `should instantiate`() {
    expect(
      CreateRunCommand(
        command = "command",
        startTime = LocalDateTime.now(),
        endTime = LocalDateTime.now(),
        branch = "branch",
        runGroup = "runGroup",
        inner = true,
        distributedExecutionId = "distributedExecutionId",
        ciExecutionId = "ciExecutionId",
        ciExecutionEnv = "ciExecutionEnv",
        machineInfo =
          MachineInfo(
            machineId = "machineId",
            platform = "platform",
            version = "version",
            cpuCores = 16,
          ),
        meta = emptyMap(),
        sha = null,
        projectGraph = null,
        vcsContext = null,
        hashedContributors = null,
        linkId = "link-id",
      ),
    ).toBeAnInstanceOf<CreateRunCommand>()
  }
}
