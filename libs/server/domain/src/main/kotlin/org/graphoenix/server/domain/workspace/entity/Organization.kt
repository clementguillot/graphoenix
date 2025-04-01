package org.graphoenix.server.domain.workspace.entity

import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

data class Organization(
  val id: OrganizationId,
  val name: String,
)
