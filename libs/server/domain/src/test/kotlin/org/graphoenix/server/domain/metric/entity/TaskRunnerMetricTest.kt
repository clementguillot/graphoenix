package org.graphoenix.server.domain.metric.entity

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TaskRunnerMetricTest {
  @Test
  fun `should build a new instance of TaskRunnerMetric`() {
    val dummyWorkspaceId = WorkspaceId("workspace-id")
    val dummyRecordingDate = LocalDateTime.now()

    val metric =
      TaskRunnerMetric {
        workspaceId = dummyWorkspaceId
        recordingDate = dummyRecordingDate
        durationMs = 100
        success = true
        statusCode = 200
        entryType = "type"
        payloadSize = 100L
      }

    expect(metric) {
      its { workspaceId }.toEqual(dummyWorkspaceId)
      its { recordingDate }.toEqual(dummyRecordingDate)
      its { durationMs }.toEqual(100)
      its { success }.toEqual(true)
      its { statusCode }.toEqual(200)
      its { entryType }.toEqual("type")
      its { payloadSize }.toEqual(100)
    }
  }
}
