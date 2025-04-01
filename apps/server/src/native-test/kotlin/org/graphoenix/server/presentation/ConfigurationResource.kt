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
    )

  override fun stop() {
    // Nothing to do here
  }
}
