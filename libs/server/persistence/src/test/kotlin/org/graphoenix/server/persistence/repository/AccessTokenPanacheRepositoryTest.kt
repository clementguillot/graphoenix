package org.graphoenix.server.persistence.repository

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bson.types.ObjectId
import org.graphoenix.server.domain.workspace.valueobject.AccessLevel
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.graphoenix.server.persistence.entity.AccessTokenEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class AccessTokenPanacheRepositoryTest {
  @Inject
  lateinit var accessTokenPanacheRepository: AccessTokenPanacheRepository

  @BeforeEach
  fun setup() {
    runBlocking {
      accessTokenPanacheRepository.deleteAll().awaitSuspending()
    }
  }

  @Test
  fun `should create a new access token in the DB`() =
    runTest {
      // Given
      val dummyWorkspaceId = WorkspaceId(ObjectId().toString())

      // When
      val accessToken = accessTokenPanacheRepository.createDefaultAccessToken(dummyWorkspaceId)

      // Then
      expect(accessToken) {
        its { accessLevel }.toEqual(AccessLevel.READ_WRITE)
        its { name }.toEqual("default")
      }
      expect(accessTokenPanacheRepository.count().awaitSuspending()).toEqual(1L)
    }

  @Test
  fun `should return access token if any`() =
    runTest {
      // Given
      val existingValue = "matching query"
      val dummyEntity =
        AccessTokenEntity(
          id = ObjectId(),
          name = "default",
          publicId = "test",
          accessLevel = "read-write",
          workspaceId = ObjectId(),
          encodedValue = existingValue,
        )
      accessTokenPanacheRepository.persist(dummyEntity).awaitSuspending()

      // When
      val matchingResult = accessTokenPanacheRepository.findByEncodedValue(existingValue)
      val nullResult = accessTokenPanacheRepository.findByEncodedValue("not found")

      // Then
      expect(matchingResult).notToEqualNull()
      expect(matchingResult!!.id.value).toEqual(dummyEntity.id.toString())
      expect(nullResult).toEqual(null)
    }
}
