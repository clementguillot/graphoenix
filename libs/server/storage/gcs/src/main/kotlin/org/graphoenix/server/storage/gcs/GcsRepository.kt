package org.graphoenix.server.storage.gcs

import com.google.auth.Credentials
import com.google.auth.ServiceAccountSigner
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
  credentials: Credentials,
) : FileRepository {
  companion object {
    private val presignExpirationDuration = 1L
    private val presignExpirationTimeUnit = TimeUnit.HOURS
  }

  init {
    require(gcsConfiguration.bucket().isPresent)
    require(credentials is ServiceAccountSigner) { "GCS credentials must be a ServiceAccountSigner for URL signing" }
  }

  private val bucket = gcsConfiguration.bucket().get()
  private val signer = credentials as ServiceAccountSigner
  private val signUrlOptions: Array<Storage.SignUrlOption> =
    buildList {
      add(Storage.SignUrlOption.withV4Signature())
      add(Storage.SignUrlOption.signWith(signer))
      val host = storage.options?.host
      if (host != null) {
        add(Storage.SignUrlOption.withHostName(host))
      }
    }.toTypedArray()

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
            *signUrlOptions,
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
            *signUrlOptions,
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
