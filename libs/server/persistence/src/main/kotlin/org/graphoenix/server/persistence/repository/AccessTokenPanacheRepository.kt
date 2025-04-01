package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.entity.AccessToken
import org.graphoenix.server.domain.workspace.gateway.AccessTokenRepository
import org.graphoenix.server.domain.workspace.valueobject.AccessLevel
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.AccessTokenEntity
import org.graphoenix.server.persistence.mapper.toDomain
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ApplicationScoped
class AccessTokenPanacheRepository :
  ReactivePanacheMongoRepository<AccessTokenEntity>,
  AccessTokenRepository {
  override suspend fun createDefaultAccessToken(workspaceId: WorkspaceId): AccessToken {
    val entity = buildDefaultAccessToken(workspaceId)
    return persist(entity).awaitSuspending().toDomain()
  }

  override suspend fun findByEncodedValue(encodedValue: String): AccessToken? {
    val entity = find(AccessTokenEntity::encodedValue.name, encodedValue).firstResult().awaitSuspending()
    return entity?.toDomain()
  }

  @OptIn(ExperimentalEncodingApi::class)
  private fun buildDefaultAccessToken(
    workspaceId: WorkspaceId,
    publicId: UUID = UUID.randomUUID(),
    accessLevel: AccessLevel = AccessLevel.READ_WRITE,
  ) = AccessTokenEntity(
    id = null,
    name = "default",
    publicId = publicId.toString(),
    accessLevel = accessLevel.value,
    workspaceId = ObjectId(workspaceId.value),
    encodedValue = Base64.encode("$publicId|${accessLevel.value}".toByteArray()),
  )
}
