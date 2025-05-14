package org.graphoenix.server.infrastructure.io

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.*
import org.jboss.logging.Logger
import java.util.zip.GZIPInputStream
import kotlin.reflect.KClass
import kotlin.text.Charsets.UTF_8

@ApplicationScoped
class GzipJsonDecoder {
  companion object {
    private val logger = Logger.getLogger(GzipJsonDecoder::class.java)
    private val objectMapper =
      jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())
  }

  suspend fun <T : Any> from(
    buffer: ByteArray,
    type: KClass<T>,
  ): T =
    coroutineScope {
      withContext(Dispatchers.IO) {
        buffer.inputStream().use { byteStream ->
          GZIPInputStream(byteStream).use { gzipStream ->
            gzipStream.bufferedReader(UTF_8).readText().let { text ->
              jsonTextToObject(text, type)
            }
          }
        }
      }
    }

  private fun <T : Any> jsonTextToObject(
    text: String,
    type: KClass<T>,
  ): T {
    try {
      return objectMapper.readValue(text, type.java)
    } catch (e: Exception) {
      logger.error("Error has occurred while processing JSON: $text", e)
      throw e
    }
  }
}
