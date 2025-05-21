package org.graphoenix.server.application.run.usecase

import jakarta.enterprise.context.ApplicationScoped
import org.graphoenix.server.application.UseCase
import org.graphoenix.server.application.run.exception.RunException
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.valueobject.LinkId

@ApplicationScoped
class GetRunByLinkId(
  private val runRepository: RunRepository,
) : UseCase<GetRunByLinkId.Request, GetRunByLinkId.Response> {
  override suspend fun <T> invoke(
    request: Request,
    presenter: (Response) -> T,
  ): T =
    presenter(
      runRepository.findByLinkId(request.linkId)?.let { run ->
        Response(run = run)
      } ?: throw RunException.NotFound("Run ${request.linkId} not found"),
    )

  data class Request(
    val linkId: LinkId,
  )

  data class Response(
    val run: Run,
  )
}
