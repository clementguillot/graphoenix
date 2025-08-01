package org.graphoenix.server.persistence.mapper

import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.valueobject.*
import org.graphoenix.server.persistence.entity.AccessTokenEntity

fun AccessTokenEntity.toDomain(): AccessToken =
  AccessToken {
    id = AccessTokenId(this@toDomain.id.toString())
    name = this@toDomain.name
    publicId = AccessTokenPublicId(this@toDomain.publicId)
    accessLevel = AccessLevel.from(this@toDomain.accessLevel)
    workspaceId = WorkspaceId(this@toDomain.workspaceId.toString())
    encodedValue = this@toDomain.encodedValue
  }
