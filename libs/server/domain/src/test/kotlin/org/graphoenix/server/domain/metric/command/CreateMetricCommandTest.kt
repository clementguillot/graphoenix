package org.graphoenix.server.domain.metric.command

import ch.tutteli.atrium.api.fluent.en_GB.toBeAnInstanceOf
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test

class CreateMetricCommandTest {
  @Test
  fun `should instantiate`() {
    expect(
      CreateMetricCommand(
        workspaceId = WorkspaceId("workspaceId"),
        durationMs = 100,
        success = true,
        statusCode = 200,
        entryType = "entryType",
        payloadSize = 100L,
      ),
    ).toBeAnInstanceOf<CreateMetricCommand>()
  }
}
