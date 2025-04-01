package org.graphoenix.server.application.workspace.exception

import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

class OrganizationNotFoundException(
  orgId: OrganizationId,
) : RuntimeException("Organization ID '${orgId.value}' not found")
