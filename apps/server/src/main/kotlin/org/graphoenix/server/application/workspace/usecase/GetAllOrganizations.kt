package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository

@ApplicationScoped
class GetAllOrganizations(
  private val organizationRepository: OrganizationRepository,
) : UseCase<Unit, GetAllOrganizations.Response> {
  override suspend fun <T> invoke(
    request: Unit,
    presenter: (Response) -> T,
  ): T = presenter(Response(organizations = organizationRepository.findAllOrgs()))

  data class Response(
    val organizations: Collection<Organization>,
  )
}
