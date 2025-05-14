package org.graphoenix.server.storage.gcs

import com.google.cloud.storage.*
import io.quarkus.arc.lookup.LookupIfProperty
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.*
import org.graphoenix.server.storage.core.FileRepository
import java.util.concurrent.TimeUnit

@LookupIfProperty(name = "graphoenix-server.storage.type", stringValue = "gcs")
@ApplicationScoped
class GcsRepository(
  gcsConfiguration: GcsConfiguration,
  private val storage: Storage,
) : FileRepository {
  companion object {
    private val presignExpirationDuration = 1L
    private val presignExpirationTimeUnit = TimeUnit.HOURS
  }

  init {
    require(gcsConfiguration.bucket().isPresent)
  }

  private val bucket = gcsConfiguration.bucket().get()

  override suspend fun generateGetUrl(objectPath: String): String =
    coroutineScope {
      withContext(Dispatchers.IO) {
        val blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, objectPath)).build()
        val url =
          storage.signUrl(
            blobInfo,
            presignExpirationDuration,
            presignExpirationTimeUnit,
            Storage.SignUrlOption.httpMethod(HttpMethod.GET),
            Storage.SignUrlOption.withV4Signature(),
          )
        url.toString()
      }
    }

  override suspend fun generatePutUrl(objectPath: String): String =
    coroutineScope {
      withContext(Dispatchers.IO) {
        val blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, objectPath)).build()
        val url =
          storage.signUrl(
            blobInfo,
            presignExpirationDuration,
            presignExpirationTimeUnit,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withV4Signature(),
          )
        url.toString()
      }
    }

  override suspend fun deleteFile(objectPath: String) {
    coroutineScope {
      withContext(Dispatchers.IO) {
        storage.delete(BlobId.of(bucket, objectPath))
      }
    }
  }
}
