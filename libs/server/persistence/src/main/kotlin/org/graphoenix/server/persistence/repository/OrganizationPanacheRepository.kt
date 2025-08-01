package org.graphoenix.server.persistence.repository

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheMongoRepository
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.entity.Organization
import org.graphoenix.server.domain.workspace.gateway.OrganizationRepository
import org.graphoenix.server.domain.workspace.valueobject.OrganizationId
import org.graphoenix.server.persistence.entity.OrganizationEntity
import org.graphoenix.server.persistence.mapper.toDomain

@ApplicationScoped
class OrganizationPanacheRepository :
  ReactivePanacheMongoRepository<OrganizationEntity>,
  OrganizationRepository {
  override suspend fun create(name: String): Organization {
    val entity = OrganizationEntity(id = null, name = name)

    return persist(entity).awaitSuspending().toDomain()
  }

  override suspend fun isValidOrgId(id: OrganizationId): Boolean = findById(ObjectId(id.value)).awaitSuspending() !== null

  override suspend fun findById(id: OrganizationId): Organization? = findById(ObjectId(id.value)).awaitSuspending()?.toDomain()

  override suspend fun findAllOrgs(): Collection<Organization> =
    findAll(Sort.by("name", Sort.Direction.Ascending))
      .list()
      .awaitSuspending()
      .map { it.toDomain() }
}
