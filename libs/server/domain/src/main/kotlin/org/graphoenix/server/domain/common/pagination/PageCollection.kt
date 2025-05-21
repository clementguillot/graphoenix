package org.graphoenix.server.domain.common.pagination

data class PageCollection<T>(
  val items: List<T>,
  val totalCount: Long,
)
