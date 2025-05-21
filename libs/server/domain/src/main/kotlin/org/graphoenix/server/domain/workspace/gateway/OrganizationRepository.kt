package org.graphoenix.server.domain.workspace.gateway

import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

interface OrganizationRepository {
  suspend fun create(name: String): Organization

  suspend fun isValidOrgId(id: OrganizationId): Boolean

  suspend fun findById(id: OrganizationId): Organization?

  suspend fun findAllOrgs(): Collection<Organization>
}
