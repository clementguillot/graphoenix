package org.graphoenix.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.uint
import io.quarkus.runtime.Quarkus
import org.graphoenix.server.presentation.cli.CleanupCommand

class Server : CliktCommand() {
  override val invokeWithoutSubcommand = true

  override fun help(context: Context) =
    """
    Welcome to Graphoenix `server`.

    By default, the application starts as a web server (same as using `web` command).
    """.trimIndent()

  override fun run() {
    currentContext.invokedSubcommand ?: startWebServer()
  }
}

class Web : CliktCommand() {
  override fun help(context: Context) =
    """
    Starts the application as a web server.

    This is the default behavior if there is no specified command or argument.
    """.trimIndent()

  override fun run() = startWebServer()
}

class Cleanup : CliktCommand() {
  private val days: UInt by option("-d", "--days", help = "Numbers of days to truncate on.")
    .uint()
    .default(30u)

  override fun help(context: Context) =
    """
    Delete a portion of trailing data based on creation date.

    All data that is older than `--days` will be deleted. The default for
    this is 30 days.
    """.trimIndent()

  override fun run() = Quarkus.run(CleanupCommand::class.java, days.toString())
}

private fun startWebServer() = Quarkus.run()
