package org.nxcloudce.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.uint
import io.quarkus.runtime.Quarkus
import org.nxcloudce.server.presentation.command.CleanupCommand

class Server : CliktCommand(
  invokeWithoutSubcommand = true,
  help =
    """
    Welcome to Nx Cloud CE `server`.

    By default, the application starts as a web server (same as using `web` command).
    """.trimIndent(),
) {
  override fun run() {
    currentContext.invokedSubcommand ?: startWebServer()
  }
}

class Web : CliktCommand(
  help =
    """
    Starts the application as a web server.

    This is the default behavior if there is no specified command or argument.
    """.trimIndent(),
) {
  override fun run() = startWebServer()
}

class Cleanup : CliktCommand(
  help =
    """
    Delete a portion of trailing data based on creation date.

    All data that is older than `--days` will be deleted. The default for
    this is 30 days.
    """.trimIndent(),
) {
  private val days: UInt by option("-d", "--days", help = "Numbers of days to truncate on.")
    .uint()
    .default(30u)

  override fun run() = Quarkus.run(CleanupCommand::class.java, days.toString())
}

private fun startWebServer() = Quarkus.run()
