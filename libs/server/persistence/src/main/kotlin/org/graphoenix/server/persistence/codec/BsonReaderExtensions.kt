package org.graphoenix.server.persistence.codec

import org.bson.BsonReader
import org.bson.BsonType

fun BsonReader.readStringArray(): List<String> =
  buildList {
    readStartArray()
    while (readBsonType() != BsonType.END_OF_DOCUMENT) {
      add(readString())
    }
    readEndArray()
  }

inline fun <T> BsonReader.readRequiredField(
  fieldName: String,
  reader: BsonReader.() -> T,
): T {
  readName(fieldName)
  return reader()
}

inline fun <T> BsonReader.readNullableField(
  fieldName: String,
  reader: BsonReader.() -> T,
): T? {
  readName(fieldName)
  return when (currentBsonType) {
    BsonType.NULL -> {
      readNull()
      null
    }
    else -> reader()
  }
}

inline fun <T> BsonReader.readDocumentMap(valueReader: BsonReader.() -> T): Map<String, T> =
  buildMap {
    readStartDocument()
    while (readBsonType() != BsonType.END_OF_DOCUMENT) {
      val key = readName()
      put(key, valueReader())
    }
    readEndDocument()
  }
