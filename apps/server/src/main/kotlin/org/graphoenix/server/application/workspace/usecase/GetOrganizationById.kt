package org.graphoenix.server.application.workspace.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.application.workspace.exception.OrganizationException
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId

@ApplicationScoped
class GetOrganizationById(
  private val organizationRepository: OrganizationRepository,
) : UseCase<GetOrganizationById.Request, GetOrganizationById.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      organizationRepository.findById(request.id)?.let { org ->
        Response(organization = org)
      } ?: throw OrganizationException.NotFound("Organization ${request.id} not found"),
    )

  data class Request(
    val id: OrganizationId,
  )

  data class Response(
    val organization: Organization,
  )
}
