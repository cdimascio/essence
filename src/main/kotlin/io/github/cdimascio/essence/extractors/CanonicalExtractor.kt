package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal object CanonicalExtractor {
    fun extract(doc: Document): String {
        val tag: Element? = doc.selectFirst("link[rel=canonical]")
        return tag?.attr("href")?.cleanse() ?: ""
    }
}
