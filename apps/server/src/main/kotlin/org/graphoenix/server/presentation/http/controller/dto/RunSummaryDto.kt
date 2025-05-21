package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import org.graphoenix.server.application.run.usecase.EndRunResponse

@RegisterForReflection
data class RunSummaryDto(
  val runUrl: String,
  val status: String,
) {
  companion object {
    fun from(
      response: EndRunResponse,
      applicationUrl: String,
    ): RunSummaryDto =
      RunSummaryDto(
        runUrl = "$applicationUrl/runs/${response.run.linkId.value}",
        status = "success",
      )
  }
}
