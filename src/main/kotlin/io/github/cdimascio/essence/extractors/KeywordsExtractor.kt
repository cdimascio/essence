package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

internal object KeywordsExtractor {
    fun extract(doc: Document): String {
        return doc.selectFirst("""meta[name=keywords]""")?.attr("content")?.cleanse() ?: ""
    }
}
