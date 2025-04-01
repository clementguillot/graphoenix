package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class HeartbeatDto(
  val ciExecutionId: String,
  val runGroup: String,
  val logs: String?,
)
