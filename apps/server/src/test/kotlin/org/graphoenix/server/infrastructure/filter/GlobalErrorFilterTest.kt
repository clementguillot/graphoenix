package org.graphoenix.server.infrastructure.filter

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.mockk.every
import io.mockk.mockk
import io.quarkus.test.InMemoryLogHandler
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import org.graphoenix.server.infrastructure.http.filter.GlobalErrorFilter
import org.junit.jupiter.api.*
import java.net.URI
import java.util.logging.*

@QuarkusTest
class GlobalErrorFilterTest {
  private val inMemoryLogHandler =
    InMemoryLogHandler { record: LogRecord ->
      record.level.intValue() >= Level.INFO.intValue()
    }
  private val rootLogger: Logger = LogManager.getLogManager().getLogger(GlobalErrorFilter::class.qualifiedName)

  @Inject
  lateinit var globalErrorFilter: GlobalErrorFilter

  @BeforeEach
  fun setupLogger() {
    rootLogger.addHandler(inMemoryLogHandler)
  }

  @AfterEach
  fun removeLogger() {
    rootLogger.removeHandler(inMemoryLogHandler)
  }

  @Test
  fun `should log something on HTTP errors`() {
    val mockRequest = mockk<ContainerRequestContext>(relaxed = true)
    every { mockRequest.uriInfo.requestUri } returns URI.create("http://localtest")
    val mockResponse = mockk<ContainerResponseContext>(relaxed = true)
    every { mockResponse.status } returns 404
    every { mockResponse.entity } returns "not found!"

    globalErrorFilter.filter(mockRequest, mockResponse)

    expect(inMemoryLogHandler.records.size).toEqual(1)
  }

  @Test
  fun `should log nothing HTTP ok codes`() {
    val mockRequest = mockk<ContainerRequestContext>(relaxed = true)
    val mockResponse = mockk<ContainerResponseContext>(relaxed = true)
    every { mockResponse.status } returns 200

    globalErrorFilter.filter(mockRequest, mockResponse)

    expect(inMemoryLogHandler.records.size).toEqual(0)
  }
}
