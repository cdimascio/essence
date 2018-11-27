package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


internal object AuthorExtractor {
    fun extract(doc: Document): List<String> {
        val candidates = doc.select("""
            meta[property='article:author'],
            meta[property='og:article:author'],
            meta[name='author'],
            meta[name='dcterms.creator'],
            meta[name='DC.creator'],
            meta[name='DC.Creator'],
            meta[name='dc.creator'],
            meta[name='creator']""".trimIndent())

        val authors = candidates.fold(mutableListOf<String>()) { l, c ->
            val author = c.attr("content").cleanse()
            if (author.isNotBlank()) {
                l.add(author)
            }
            l
        }

        if (authors.isEmpty()) {
            val fallback: Element? = doc.selectFirst("span[class*='author']")
                ?: doc.selectFirst("p[class*='author']")
                ?: doc.selectFirst("div[class*='author']")
                ?: doc.selectFirst("span[class*='byline']")
                ?: doc.selectFirst("p[class*='byline']")
                ?: doc.selectFirst("div[class*='byline']")
            fallback?.let {
                authors.add(fallback.text().cleanse())
            }
        }
        return authors
    }
}
