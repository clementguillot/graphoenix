package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class InitWorkspaceDto(
  val url: String,
  val token: String,
)
