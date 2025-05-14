package org.graphoenix.server.storage.s3

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.net.url.Url
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@QuarkusTest
class S3RepositoryTest {
  @ConfigProperty(name = "quarkus.s3.endpoint-override")
  lateinit var s3EndpointOverride: String

  @Inject
  lateinit var s3Configuration: S3Configuration
  lateinit var s3Client: S3Client
  lateinit var s3Repository: S3Repository

  @BeforeEach
  fun setUp() {
    s3Client =
      S3Client {
        endpointUrl = Url.parse(s3EndpointOverride)
        region = s3Configuration.region().get()
        forcePathStyle = s3Configuration.forcePathStyle().map { it }.orElse(null)
        credentialsProvider =
          StaticCredentialsProvider {
            accessKeyId = s3Configuration.accessKeyId().get()
            secretAccessKey = s3Configuration.secretAccessKey().get()
          }
      }
    s3Repository =
      S3Repository(
        object : S3Configuration {
          override fun endpoint(): Optional<String> = Optional.of(s3EndpointOverride)

          override fun region(): Optional<String> = s3Configuration.region()

          override fun accessKeyId(): Optional<String> = s3Configuration.accessKeyId()

          override fun secretAccessKey(): Optional<String> = s3Configuration.secretAccessKey()

          override fun bucket(): Optional<String> = s3Configuration.bucket()

          override fun forcePathStyle(): Optional<Boolean> = s3Configuration.forcePathStyle()
        },
      )
  }

  @Test
  fun `should presign a GET URL`() =
    runTest {
      // When
      val result = s3Repository.generateGetUrl("file-path")

      // Then
      expect(result).toStartWith("$s3EndpointOverride/graphoenix-test/file-path?x-id=GetObject")
    }

  @Test
  fun `should presign a PUT URL`() =
    runTest {
      // When
      val result = s3Repository.generatePutUrl("file-path")

      // Then
      expect(result).toStartWith("$s3EndpointOverride/graphoenix-test/file-path?x-id=PutObject")
    }

  @Test
  fun `should delete a file`() =
    runTest {
      // Given
      val dummyRequest =
        PutObjectRequest {
          bucket = "graphoenix-test"
          key = "file-path"
          body = ByteStream.fromString("DUMMY FILE!")
        }
      s3Client.putObject(dummyRequest)
      val getRequest =
        GetObjectRequest {
          bucket = "graphoenix-test"
          key = "file-path"
        }
      val beforeResponse = s3Client.getObject(getRequest) { it }
      expect(beforeResponse.contentLength).notToEqualNull()

      // When
      s3Repository.deleteFile("file-path")

      // Then
      expect {
        runBlocking {
          s3Client.getObject(getRequest) {}
        }
      }.toThrow<NoSuchKey>()
    }
}
