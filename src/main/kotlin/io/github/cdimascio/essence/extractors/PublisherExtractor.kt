package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

internal object PublisherExtractor {
    fun extract(doc: Document): String {
        val candidates = doc.select("""
            meta[property='og:site_name'],
            meta[name='dc.publisher'],
            meta[name='DC.publisher'],
            meta[name='DC.Publisher']""".trimIndent())
        for (c in candidates) {
            val text = c.attr("content").cleanse()
            if (text.isNotBlank()) return text
        }
        return ""
    }
}
