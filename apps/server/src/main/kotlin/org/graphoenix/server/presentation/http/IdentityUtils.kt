package org.graphoenix.server.presentation.http

import io.quarkus.security.identity.CurrentIdentityAssociation
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.graphoenix.server.domain.workspace.valueobject.WorkspaceId

suspend fun CurrentIdentityAssociation.getWorkspaceId(): WorkspaceId = WorkspaceId(deferredIdentity.awaitSuspending().principal.name)
