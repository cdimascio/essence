package io.github.cdimascio.essence.extractors

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal object TitleExtractor {
    fun extract(doc: Document) = cleanTitle(rawTitle(doc), listOf("|", " - ", ">>", ":"))
}

internal object SoftTitleExtractor {
    fun extract(doc: Document) = cleanTitle(rawTitle(doc), listOf("|", " - ", "»"))
}

private fun rawTitle(doc: Document): String {

    val candidates = mutableListOf<Element>()
    candidates.addAll(doc.select("""meta[property='og:title']""").toList())

    doc.selectFirst("h1[class*='title']")?.let {
        candidates.add(it)
    }
    doc.selectFirst("title")?.let {
        candidates.add(it)
    }
    // The first h1 or h2 is a useful fallback
    doc.selectFirst("h1")?.let {
        candidates.add(it)
    }
    doc.selectFirst("h2")?.let {
        candidates.add(it)
    }

    var text: String? = null
    for (c in candidates) {
        text = c.attr("content")
        if (text.isBlank()) text = c.text()
        if (text.isNotBlank()) break
    }
    return text?.cleanse() ?: ""
}

private fun cleanTitle(title: String?, delimiters: List<String> = emptyList()): String {
    var t = title ?: ""
    for (d in delimiters) {
        if (t.contains(d)) {
            t = biggestTitleChunk(t, d)
            break
        }
    }
    return t.cleanse()
}

// Find the biggest chunk of text in the title
private fun biggestTitleChunk(title: String, delimiter: String): String {
    val titleParts = title.split(delimiter)

    // find the largest substring
    var largestPart = ""
    for (part in titleParts) {
        if (part.length > largestPart.length) {
            largestPart = part
        }
    }
    return largestPart
}
