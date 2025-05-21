package org.graphoenix.server.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.asFlow
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.graphoenix.server.domain.common.pagination.PageCollection
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.valueobject.LinkId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.RunEntity
import org.graphoenix.server.persistence.mapper.toDomain
import org.graphoenix.server.persistence.mapper.toEntity
import java.time.LocalDateTime

@ApplicationScoped
class RunPanacheRepository :
  ReactivePanacheMongoRepository<RunEntity>,
  RunRepository {
  companion object {
    private val objectMapper = jacksonObjectMapper()
  }

  override fun findAllByCreationDateOlderThan(date: LocalDateTime): Flow<Run> =
    find("endTime < ?1", date).stream().asFlow().map { it.toDomain(objectMapper) }

  override suspend fun findPageByWorkspaceId(
    workspaceId: WorkspaceId,
    pageIndex: Int,
    pageSize: Int,
  ): PageCollection<Run> =
    coroutineScope {
      find("workspaceId", Sort.by("endTime", Sort.Direction.Descending), ObjectId(workspaceId.value))
        .page(Page(pageIndex, pageSize))
        .let { query ->
          val result = async { query.list().awaitSuspending() }
          val totalCount = async { query.count().awaitSuspending() }
          PageCollection(
            items = result.await().map { it.toDomain(objectMapper) },
            totalCount = totalCount.await(),
          )
        }
    }

  override suspend fun findByLinkId(linkId: LinkId): Run? =
    find(
      "linkId",
      linkId.value,
    ).firstResult().awaitSuspending()?.toDomain(objectMapper)

  override suspend fun create(
    run: CreateRunCommand,
    status: Int,
    workspaceId: WorkspaceId,
  ): Run {
    val entity = run.toEntity(status, workspaceId, objectMapper)

    return persist(entity).awaitSuspending().toDomain(objectMapper)
  }

  override suspend fun delete(run: Run) = deleteById(ObjectId(run.id.value)).awaitSuspending()
}
