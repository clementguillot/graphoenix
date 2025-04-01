package org.graphoenix.server.infrastructure.io

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

@QuarkusTest
class GzipJsonDecoderTest {
  @Inject
  lateinit var gzipDecoder: GzipJsonDecoder

  @Test
  fun `should uncompress and map a strongly-typed object from JSON`() =
    runTest {
      val dummyObject = DummyClass("test value", 42)

      val outputStream = ByteArrayOutputStream()
      GZIPOutputStream(outputStream).bufferedWriter().use {
        it.write(
          """
          {
            "stringProperty": "test value",
            "intProperty":42
          }
          """.trimIndent(),
        )
      }

      val result = gzipDecoder.from(outputStream.toByteArray(), DummyClass::class)
      expect(result).toEqual(dummyObject)
    }

  @Test
  fun `should throw an error if input JSON does not match supplied type`() =
    runTest {
      val outputStream = ByteArrayOutputStream()
      GZIPOutputStream(outputStream).bufferedWriter().use {
        it.write(
          """
          {
            "invalid": "property"
          }
          """.trimIndent(),
        )
      }

      expect {
        runBlocking {
          gzipDecoder.from(outputStream.toByteArray(), DummyClass::class)
        }
      }.toThrow<Exception>()
    }

  data class DummyClass(
    val stringProperty: String,
    val intProperty: Int,
  )
}
