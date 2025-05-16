package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.persistence.entity.WorkspaceEntity
import org.graphoenix.server.persistence.mapper.toDomain
import java.time.LocalDateTime

@ApplicationScoped
class WorkspacePanacheRepository :
  ReactivePanacheMongoRepository<WorkspaceEntity>,
  WorkspaceRepository {
  override suspend fun create(
    name: String,
    orgId: OrganizationId,
  ): Workspace {
    val entity =
      WorkspaceEntity(
        id = null,
        orgId = ObjectId(orgId.value),
        name = name,
        installationSource = null,
        nxInitDate = null,
      )

    return persist(entity).awaitSuspending().toDomain()
  }

  override suspend fun create(
    name: String,
    installationSource: String,
    nxInitDate: LocalDateTime?,
    orgId: OrganizationId,
  ): Workspace {
    val entity = WorkspaceEntity(null, ObjectId(orgId.value), name, installationSource, nxInitDate)
    return persist(entity).awaitSuspending().toDomain()
  }
}
