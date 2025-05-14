package org.graphoenix.server.infrastructure.persistence

import io.quarkus.vertx.core.runtime.context.VertxContextSafetyToggle
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.mutiny.coroutines.uni
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextInternal
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import org.jboss.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun <T> withTransaction(crossinline block: suspend () -> T): T {
  val context = Vertx.currentContext()

  if (context == null) {
    Logger.getLogger("withTransaction").warn("No Vertx context found")
    return block()
  }
  context.let { it as ContextInternal }.duplicate().also { VertxContextSafetyToggle.setContextSafe(it, true) }

  return coroutineScope {
    // uses vertx dispatcher and preserves slf4j MDC context
    async(context.dispatcher() + MDCContext()) {
      // io.quarkus.mongodb.panache.common.reactive.Panache.withTransaction
      io.quarkus.mongodb.panache.common.reactive.Panache
        .withTransaction {
          // this is CoroutineScope
          uni(this) {
            block()
          }
        }.awaitSuspending() // awaits the Uni from withTransaction
    }.await() // awaits the Deferred from async
  }
}
