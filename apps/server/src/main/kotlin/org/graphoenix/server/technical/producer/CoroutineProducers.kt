package org.graphoenix.server.technical.producer

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@ApplicationScoped
class CoroutineProducers {
  @Produces
  fun coroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
