package org.graphoenix.server.infrastructure.persistence

import ch.tutteli.atrium.api.fluent.en_GB.notToEqualNull
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@QuarkusTest
class WithTransactionKtTest {
  @OptIn(DelicateCoroutinesApi::class)
  @Test
  @RunOnVertxContext
  fun `should execute a suspend block on Vertx context`() =
    runTest {
      // Given
      val vertx = Vertx.currentContext()

      GlobalScope.launch(vertx.dispatcher()) {
        val block =
          suspend {
            delay(500)
            Vertx.currentContext()
          }
        // When
        val result = withTransaction(block)

        // Then
        expect(result).notToEqualNull()
      }
    }

  @Test
  fun `should execute a block when there is no Vertx context`() =
    runTest {
      // When
      val result = withTransaction { Vertx.currentContext() }

      // Then
      expect(result).toEqual(null)
    }
}
