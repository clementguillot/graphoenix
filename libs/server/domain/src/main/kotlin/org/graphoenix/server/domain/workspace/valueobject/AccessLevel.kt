package org.graphoenix.server.domain.workspace.valueobject

enum class AccessLevel(
  val value: String,
) {
  READ_ONLY("read"),
  READ_WRITE("read-write"),
  ;

  companion object {
    fun from(value: String): AccessLevel = entries.find { it.value == value } ?: throw IllegalArgumentException()
  }
}
