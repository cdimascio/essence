package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

private val REGEX_COPYRIGHT = """.*?©(\s*copyright)?([^,;:.|\r\n]+).*""".toRegex(RegexOption.IGNORE_CASE)

internal object CopyrightExtractor {
    fun extract(doc: Document): String {
        val candidate: Element? = doc.selectFirst("""
            p[class*='copyright'],
            div[class*='copyright'],
            span[class*='copyright'],
            li[class*='copyright'],
            p[id*='copyright'],
            div[id*='copyright'],
            span[id*='copyright'],
            li[id*='copyright']
            """.trimIndent())
        var text = candidate?.text() ?: ""
        if (text.isBlank()) {
            val bodyText = doc.body().text().replace("""s*[\r\n]+\s*""".toRegex(), ". ")
            if (bodyText.contains("©")) {
                text = bodyText
            }
        }
        val match = REGEX_COPYRIGHT.find(text)
        val copyright = match?.groupValues?.get(2) ?: ""
        return copyright.cleanse()
    }
}

