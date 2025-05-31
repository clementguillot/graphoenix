package org.graphoenix.server.presentation.http.controller.dto

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class PageCollectionDto<T>(
  val items: List<T>,
  val totalCount: Long,
)
