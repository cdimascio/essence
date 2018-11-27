package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document

internal object DataExtractor {
    fun extract(doc: Document): String {
        val candidates = doc.select("""
            meta[property='article:published_time'],
            meta[itemprop*='datePublished'], meta[name='dcterms.modified'],
            meta[name='dcterms.date'],
            meta[name='DC.date.issued'],  meta[name='dc.date.issued'],
            meta[name='dc.date.modified'], meta[name='dc.date.created'],
            meta[name='DC.date'],
            meta[name='DC.Date'],
            meta[name='dc.date'],
            meta[name='date'],
            time[itemprop*='pubDate'],
            time[itemprop*='pubdate'],
            span[itemprop*='datePublished'],
            span[property*='datePublished'],
            p[itemprop*='datePublished'],
            p[property*='datePublished'],
            div[itemprop*='datePublished'],
            div[property*='datePublished'],
            li[itemprop*='datePublished'],
            li[property*='datePublished'],
            time,
            span[class*='d  ate'],
            p[class*='date'],
            div[class*='date']
        """.trimIndent())

        for (dateCandidate in candidates) {
            val content = dateCandidate.attr("content").cleanse()
            if (content.isNotBlank()) return content

            val datetime = dateCandidate.attr("datetime").cleanse()
            if (datetime.isNotBlank()) return datetime

            val text = dateCandidate.text().cleanse()
            if (text.isNotBlank()) return text

        }
        return ""
    }
}
