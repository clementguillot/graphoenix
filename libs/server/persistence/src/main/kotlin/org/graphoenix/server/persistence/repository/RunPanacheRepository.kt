package org.graphoenix.server.persistence.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.smallrye.mutiny.coroutines.asFlow
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.graphoenix.server.domain.run.command.CreateRunCommand
import org.graphoenix.server.domain.run.entity.Run
import org.graphoenix.server.domain.run.gateway.RunRepository
import org.graphoenix.server.domain.run.valueobject.RunStatus
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

  override suspend fun create(
    run: CreateRunCommand,
    status: RunStatus,
    workspaceId: WorkspaceId,
  ): Run {
    val entity = run.toEntity(status, workspaceId, objectMapper)

    return persist(entity).awaitSuspending().run { entity.toDomain(objectMapper) }
  }

  override fun findAllByCreationDateOlderThan(date: LocalDateTime): Flow<Run> =
    find("${RunEntity::endTime.name} < ?1", date).stream().asFlow().map { it.toDomain(objectMapper) }

  override suspend fun delete(run: Run) = deleteById(ObjectId(run.id.value)).awaitSuspending()
}
