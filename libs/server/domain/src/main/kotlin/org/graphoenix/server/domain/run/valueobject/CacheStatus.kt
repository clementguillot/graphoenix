package org.graphoenix.server.domain.run.valueobject

enum class CacheStatus(
  val value: String,
) {
  REMOTE_CACHE_HIT("remote-cache-hit"),
  LOCAL_CACHE_HIT("local-cache-hit"),
  CACHE_MISS("cache-miss"),
  ;

  companion object {
    fun from(value: String): CacheStatus = entries.find { it.value == value } ?: throw IllegalArgumentException()
  }
}
