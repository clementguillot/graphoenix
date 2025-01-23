package org.graphoenix.server.presentation.controller

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.CurrentIdentityAssociation
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.graphoenix.server.domain.metric.usecase.SaveMetrics
import org.graphoenix.server.domain.metric.usecase.SaveMetricsRequest
import org.graphoenix.server.presentation.dto.TaskRunnerMetricDto
import org.graphoenix.server.technical.getWorkspaceId

@Path("")
@Authenticated
class MetricController(
  private val identity: CurrentIdentityAssociation,
  private val saveMetrics: SaveMetrics,
) {
  @Operation(summary = "Stores metrics collected by the task runner")
  @POST
  @Path("/save-metrics")
  @Consumes("application/json")
  suspend fun save(metrics: TaskRunnerMetricDto): Response =
    saveMetrics(
      SaveMetricsRequest(
        metrics = metrics.toRequest(identity.getWorkspaceId()),
      ),
    ) {
      Response.ok().build()
    }
}
