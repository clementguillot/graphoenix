package org.graphoenix.server.presentation.http.controller

import io.quarkus.test.common.WithTestResource
import io.quarkus.test.junit.QuarkusIntegrationTest
import org.graphoenix.server.presentation.ConfigurationResource

// TODO: will be fixed by #119
@QuarkusIntegrationTest
@WithTestResource(ConfigurationResource::class)
class WorkspaceControllerIT // : WorkspaceControllerTest()
