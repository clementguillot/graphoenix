package org.graphoenix.server.presentation.cli

import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.main.QuarkusMainLauncher
import io.quarkus.test.junit.main.QuarkusMainTest
import org.junit.jupiter.api.Test

@QuarkusMainTest
open class CleanupCommandTest {
  @Test
  fun `should cleanup old runs`(launcher: QuarkusMainLauncher) {
    // This test is actually not very helpful because if we don't want to use @Inject here,
    // we need to start a web server and call "end-run" endpoint to build dataset.
    // So, we are only going to ensure that our application doesn't crash, even if there is
    // nothing to clean-up.

    // When
    val result = launcher.launch("cleanup")

    // Then
    expect(result) {
      its { exitCode() }.toEqual(0)
      its { output }.toContain("Cleanup ended! # of deleted runs: 0")
    }
  }
}
