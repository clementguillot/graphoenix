package org.graphoenix.server.domain.common.pagination

import ch.tutteli.atrium.api.fluent.en_GB.toBeAnInstanceOf
import ch.tutteli.atrium.api.verbs.expect
import org.junit.jupiter.api.Test

class PageCollectionTest {
  @Test
  fun `should instantiate`() {
    expect(PageCollection(items = listOf("A", "B"), totalCount = 2)).toBeAnInstanceOf<PageCollection<String>>()
  }
}
