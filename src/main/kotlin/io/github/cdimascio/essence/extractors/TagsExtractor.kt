package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

internal object TagsExtractor {
    fun extract(doc: Document): List<String> {
        var candidates = doc.select("a[rel='tag']")
        if (candidates.isEmpty()) {
            candidates = doc.select("""
                a[href*='/tag/'],
                a[href*='/tags/'],
                a[href*='/topic/'],
                a[href*='?keyword=']
            """.trimIndent())
        }

        if (candidates.isEmpty()) {
            return emptyList()
        }

        return candidates.map {
            it.text().cleanse().replace("""[\s\t\n]+""".toRegex(), " ")
        }.filter {
            it.isNotBlank()
        }.distinct()
    }
}
