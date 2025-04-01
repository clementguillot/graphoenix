package org.graphoenix.server.storage.core

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.*
import io.quarkus.test.junit.QuarkusTest
import jakarta.enterprise.inject.Instance
import jakarta.enterprise.util.TypeLiteral
import kotlinx.coroutines.test.runTest
import org.graphoenix.server.domain.run.valueobject.ArtifactId
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId
import org.junit.jupiter.api.Test

@QuarkusTest
class StorageServiceImplTest {
  private val mockFileRepository = mockk<FileRepository>()
  private val storageServiceImpl =
    StorageServiceImpl(
      object : Instance<FileRepository> {
        override fun get(): FileRepository = mockFileRepository

        override fun iterator(): MutableIterator<FileRepository> {
          TODO("Not yet implemented")
        }

        override fun select(vararg qualifiers: Annotation?): Instance<FileRepository> {
          TODO("Not yet implemented")
        }

        override fun isUnsatisfied(): Boolean {
          TODO("Not yet implemented")
        }

        override fun isAmbiguous(): Boolean {
          TODO("Not yet implemented")
        }

        override fun getHandle(): Instance.Handle<FileRepository> {
          TODO("Not yet implemented")
        }

        override fun handles(): MutableIterable<Instance.Handle<FileRepository>> {
          TODO("Not yet implemented")
        }

        override fun destroy(instance: FileRepository?) {
          TODO("Not yet implemented")
        }

        override fun <U : FileRepository?> select(
          subtype: TypeLiteral<U>?,
          vararg qualifiers: Annotation?,
        ): Instance<U> {
          TODO("Not yet implemented")
        }

        override fun <U : FileRepository?> select(
          subtype: Class<U>?,
          vararg qualifiers: Annotation?,
        ): Instance<U> {
          TODO("Not yet implemented")
        }
      },
    )

  @Test
  fun `should return GET URL from file repository`() =
    runTest {
      // Given
      coEvery { mockFileRepository.generateGetUrl("dummy-workspace/dummy-artifact") } returns "get-presigned-url"

      // When
      val result = storageServiceImpl.generateGetUrl(ArtifactId("dummy-artifact"), WorkspaceId("dummy-workspace"))

      // Then
      expect(result).toEqual("get-presigned-url")
      coVerify(exactly = 1) { mockFileRepository.generateGetUrl("dummy-workspace/dummy-artifact") }
    }

  @Test
  fun `should return PUT URL from file repository`() =
    runTest {
      // Given
      coEvery { mockFileRepository.generatePutUrl("dummy-workspace/dummy-artifact") } returns "put-presigned-url"

      // When
      val result = storageServiceImpl.generatePutUrl(ArtifactId("dummy-artifact"), WorkspaceId("dummy-workspace"))

      // Then
      expect(result).toEqual("put-presigned-url")
      coVerify(exactly = 1) { mockFileRepository.generatePutUrl("dummy-workspace/dummy-artifact") }
    }

  @Test
  fun `should delete an object from file repository`() =
    runTest {
      // Given
      coEvery { mockFileRepository.deleteFile("dummy-workspace/dummy-artifact") } just runs

      // When
      storageServiceImpl.deleteArtifact(ArtifactId("dummy-artifact"), WorkspaceId("dummy-workspace"))

      // Then
      coVerify(exactly = 1) { mockFileRepository.deleteFile("dummy-workspace/dummy-artifact") }
    }
}
