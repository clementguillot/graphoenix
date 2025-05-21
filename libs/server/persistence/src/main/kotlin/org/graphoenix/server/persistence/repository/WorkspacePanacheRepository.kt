package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.entity.Workspace
import org.graphoenix.server.domain.workspace.gateway.WorkspaceRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
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

  override suspend fun findAllByOrgId(orgId: OrganizationId): Collection<Workspace> =
    find("orgId", Sort.by("name", Sort.Direction.Ascending), ObjectId(orgId.value)).list().awaitSuspending().map {
      it.toDomain()
    }

  override suspend fun findByIdAndOrgId(
    id: WorkspaceId,
    orgId: OrganizationId,
  ): Workspace? =
    find(
      "_id = ?1 and orgId = ?2",
      ObjectId(id.value),
      ObjectId(orgId.value),
    )
      // We should use the java method "singleResultOptional()" here,
      // which is more appropriate for this use case.
      // Unfortunately, it is not available thought Kotlin API.
      .firstResult()
      .awaitSuspending()
      ?.toDomain()
}
