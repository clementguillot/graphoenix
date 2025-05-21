package org.graphoenix.server.presentation

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

// This is a workaround (from https://github.com/quarkusio/quarkus/issues/24581#issuecomment-1380467568).
// During native tests, Quarkus use "prod" configuration.
// However, we need to set some properties to avoid runtime errors.
class ConfigurationResource : QuarkusTestResourceLifecycleManager {
  override fun start(): Map<String, String> =
    mapOf(
      "graphoenix-server.configuration.application-url" to "http://localtest",
      "graphoenix-server.configuration.client-bundle-version" to "dummy-version",
      "graphoenix-server.configuration.client-bundle-path" to "static/client-bundle.gz",
      "graphoenix-server.storage.type" to "s3",
      "graphoenix-server.storage.s3.endpoint" to "http://localhost:4566",
      "graphoenix-server.storage.s3.region" to "us-east-1",
      "graphoenix-server.storage.s3.access-key-id" to "test-key",
      "graphoenix-server.storage.s3.secret-access-key" to "test-secret",
      "graphoenix-server.storage.s3.bucket" to "default",
      "graphoenix-server.storage.s3.force-path-style" to "true",
    )

  override fun stop() {
    // Nothing to do here
  }
}
