package org.graphoenix.server.presentation.cli

import io.quarkus.test.common.WithTestResource
import io.quarkus.test.junit.QuarkusIntegrationTest
import org.graphoenix.server.presentation.ConfigurationResource

@QuarkusIntegrationTest
@WithTestResource(ConfigurationResource::class)
class CleanupCommandTestIT : CleanupCommandTest()
