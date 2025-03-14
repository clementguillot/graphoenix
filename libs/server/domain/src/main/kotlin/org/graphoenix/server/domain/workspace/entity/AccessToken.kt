package org.graphoenix.server.domain.workspace.entity

import org.graphoenix.server.domain.workspace.valueobject.AccessLevel
import org.graphoenix.server.domain.workspace.valueobject.AccessTokenId
import org.graphoenix.server.domain.workspace.valueobject.AccessTokenPublicId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

class AccessToken private constructor(
  builder: Builder,
) {
  companion object {
    operator fun invoke(block: Builder.() -> Unit): AccessToken {
      val builder = Builder()
      block(builder)
      return builder.build()
    }
  }

  val id: AccessTokenId
  val name: String
  val publicId: AccessTokenPublicId
  val accessLevel: AccessLevel
  val workspaceId: WorkspaceId
  val encodedValue: String

  init {
    requireNotNull(builder.id)
    requireNotNull(builder.name)
    requireNotNull(builder.publicId)
    requireNotNull(builder.accessLevel)
    requireNotNull(builder.workspaceId)
    requireNotNull(builder.encodedValue)
    id = builder.id!!
    name = builder.name!!
    publicId = builder.publicId!!
    accessLevel = builder.accessLevel!!
    workspaceId = builder.workspaceId!!
    encodedValue = builder.encodedValue!!
  }

  class Builder {
    var id: AccessTokenId? = null
    var name: String? = null
    var publicId: AccessTokenPublicId? = null
    var accessLevel: AccessLevel? = null
    var workspaceId: WorkspaceId? = null
    var encodedValue: String? = null

    fun build() = AccessToken(this)
  }
}
