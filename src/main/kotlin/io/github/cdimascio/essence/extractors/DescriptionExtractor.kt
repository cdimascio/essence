package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

internal object DescriptionExtractor {
    fun extract(doc: Document): String {
        return doc.selectFirst("""
           meta[name=description],
           meta[property='og:description']
            """.trimIndent())?.attr("content")?.cleanse() ?: ""
    }
}
