package org.graphoenix.server.domain.workspace.entity

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.junit.jupiter.api.Test

class OrganizationTest {
  @Test
  fun `should build a new instance of Organization`() {
    val organization =
      Organization(
        id = OrganizationId("organization-id"),
        name = "organization-name",
      )

    expect(organization) {
      its { id.value }.toEqual("organization-id")
      its { name }.toEqual("organization-name")
    }
  }
}
