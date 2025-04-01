package org.graphoenix.server.domain.workspace.entity

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WorkspaceTest {
  @Test
  fun `should build a new instance of Workspace`() {
    val initDate = LocalDateTime.now()
    val workspace =
      Workspace(
        id = WorkspaceId("workspace-id"),
        orgId = OrganizationId("organization-id"),
        name = "workspace-name",
        installationSource = "installation-source",
        nxInitDate = initDate,
      )

    expect(workspace) {
      its { id.value }.toEqual("workspace-id")
      its { orgId.value }.toEqual("organization-id")
      its { name }.toEqual("workspace-name")
      its { installationSource }.toEqual("installation-source")
      its { nxInitDate }.toEqual(initDate)
    }
  }
}
