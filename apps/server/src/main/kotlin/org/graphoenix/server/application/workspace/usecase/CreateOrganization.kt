package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository

@ApplicationScoped
class CreateOrganization(
  private val orgGateway: OrganizationRepository,
) : UseCase<CreateOrganizationRequest, CreateOrganizationResponse> {
  override suspend operator fun <T> invoke(
    request: CreateOrganizationRequest,
    presenter: (CreateOrganizationResponse) -> T,
  ): T {
    val org = orgGateway.create(request.name)
    return presenter(CreateOrganizationResponse(org))
  }
}

data class CreateOrganizationRequest(
  val name: String,
)

data class CreateOrganizationResponse(
  val organization: Organization,
)
