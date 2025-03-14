package org.graphoenix.server.storage.gcs

import com.google.cloud.NoCredentials
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageOptions
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.eclipse.microprofile.config.ConfigProvider
import org.testcontainers.containers.GenericContainer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

class GcsEmulatorResource : QuarkusTestResourceLifecycleManager {
  private lateinit var container: GenericContainer<*>

  override fun start(): Map<String, String> {
    container =
      GenericContainer("fsouza/fake-gcs-server:1.49.3")
        .withExposedPorts(4443)
        .withCommand("-scheme", "http")
    container.start()

    val emulatorHost = "http://${container.host}:${container.firstMappedPort}"

    val bucket = ConfigProvider.getConfig().getValue("graphoenix-server.storage.gcs.bucket", String::class.java)

    updateExternalUrlWithContainerUrl(emulatorHost)
    createBucket(emulatorHost, bucket)

    return mapOf(
      "quarkus.google.cloud.storage.host-override" to emulatorHost,
    )
  }

  override fun stop() {
    container.stop()
  }

  private fun updateExternalUrlWithContainerUrl(fakeGcsExternalUrl: String) {
    val modifyExternalUrlRequestUri = "$fakeGcsExternalUrl/_internal/config"
    val updateExternalUrlJson =
      """
      {
        "externalUrl": "$fakeGcsExternalUrl"
      }
      """.trimIndent()

    val req =
      HttpRequest
        .newBuilder()
        .uri(URI.create(modifyExternalUrlRequestUri))
        .header("Content-Type", "application/json")
        .PUT(BodyPublishers.ofString(updateExternalUrlJson))
        .build()
    val response =
      HttpClient
        .newBuilder()
        .build()
        .send(req, BodyHandlers.discarding())

    if (response.statusCode() != 200) {
      throw RuntimeException(
        "error updating fake-gcs-server with external url, response status code " + response.statusCode() + " != 200",
      )
    }
  }

  private fun createBucket(
    endpoint: String,
    bucket: String,
  ) {
    val storage =
      StorageOptions
        .newBuilder()
        .setHost(endpoint)
        .setProjectId("test-project")
        .setCredentials(NoCredentials.getInstance())
        .build()
        .service
    storage.create(BucketInfo.newBuilder(bucket).build())
  }
}
