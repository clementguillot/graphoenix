package org.graphoenix.server.storage.gcs

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toStartWith
import ch.tutteli.atrium.api.verbs.expect
import com.google.cloud.storage.*
import io.quarkus.test.common.WithTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

@QuarkusTest
@WithTestResource(GcsEmulatorResource::class)
class GcsRepositoryTest {
  @ConfigProperty(name = "quarkus.google.cloud.storage.host-override")
  lateinit var gcsEndpoint: String

  @ConfigProperty(name = "graphoenix-server.storage.gcs.bucket")
  lateinit var gcsBucket: String

  @Inject
  lateinit var storage: Storage

  @Inject
  lateinit var gcsRepository: GcsRepository

  @Test
  fun `should presign a GET URL`() =
    runTest {
      // When
      val result = gcsRepository.generateGetUrl("file-path").replace("https://", "http://")

      // Then
      expect(result).toStartWith("$gcsEndpoint/$gcsBucket/file-path?X-Goog-Algorithm=GOOG4-RSA-SHA256")
    }

  @Test
  fun `should presign a PUT URL`() =
    runTest {
      // When
      val result = gcsRepository.generatePutUrl("file-path").replace("https://", "http://")

      // Then
      expect(result).toStartWith("$gcsEndpoint/$gcsBucket/file-path?X-Goog-Algorithm=GOOG4-RSA-SHA256")
    }

  @Test
  fun `should delete a file`() =
    runTest {
      // Given
      storage.createFrom(
        BlobInfo.newBuilder(BlobId.of(gcsBucket, "file-path")).build(),
        ByteArrayInputStream("DUMMY FILE!".toByteArray()),
        Storage.BlobWriteOption.crc32cMatch(),
      )

      val blob = storage.get(gcsBucket, "file-path")
      expect(blob.exists()).toEqual(true)

      // When
      gcsRepository.deleteFile("file-path")

      // Then
      val deletedBlog = storage.get(gcsBucket, "file-path")
      expect(deletedBlog).toEqual(null)
    }
}
