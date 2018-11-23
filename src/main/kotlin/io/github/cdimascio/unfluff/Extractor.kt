package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

private val REGEX_COPYRIGHT = """.*?©(\s*copyright)?([^,;:.|\r\n]+).*""".toRegex(RegexOption.IGNORE_CASE)

class Extractor(private val doc: Document, private val language: Language = Language.en) {
    private val stopWords: StopWords = StopWords.load(language)
    private val formatter = Formatter(doc, language, stopWords)

    init {
    }

    private fun parseDoc() {

    }

    fun date(): String? {
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
        return null
    }

    fun copyright(): String {
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


    fun authors(): List<String> {
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

    fun publisher(): String? {
        val candidates = doc.select("""
            meta[property='og:site_name'],
            meta[name='dc.publisher'],
            meta[name='DC.publisher'],
            meta[name='DC.Publisher']""".trimIndent())
        for (c in candidates) {
            val text = c.attr("content").cleanse()
            if (text.isNotBlank()) return text
        }
        return null
    }

    fun title(): String? {
        return cleanTitle(rawTitle(), listOf("|", " - ", ">>", ":"))
    }

    fun softTitle(): String? {
        return cleanTitle(rawTitle(), listOf("|", " - ", "»"))
    }

    fun text(node: Element?): String {
        return node?.let {
//            scorer.filterNodes()
            formatter.format(node)
        } ?: ""
    }

    private fun rawTitle(): String? {

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
            text = c.attr("content").cleanse()
            if (text.isBlank()) text = c.text().cleanse()
            if (text.isNotBlank()) return text
        }
        return text
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

    fun favicon(): String {
        val favicon = doc.select("link").filter{
            it.attr("rel").toLowerCase() == "shortcut icon"
        }
        if (favicon.isNotEmpty()) {
            return favicon[0].attr("href")
        }
        return ""
    }

    fun description(): String {
        return doc.selectFirst("""
            meta[name=description],
            meta[property='og:description']
            """.trimIndent())?.
            attr("content")?.
            cleanse() ?: ""
    }

    fun keywords() {

    }

    fun lang() {

    }

    fun canonicalLink() {

    }

    fun tags() {

    }

    fun image(): String? {
        val candidate: Element? = doc.selectFirst("""
            meta[property='og:image'],
            meta[property='og:image:url'],
            meta[itemprop=image],
            meta[name='twitter:image:src'],
            meta[name='twitter:image'],
            meta[name='twitter:image0']
        """.trimIndent())
        return candidate?.attr("content")?.cleanse()
    }

    fun videos() {

    }

    fun links() {

    }

    fun ext() {

    }

    fun String.cleanse(): String {
        return this.trim().replace("null", "").replace("""[\r\n\t]""".toRegex(), " ").replace("""\s\s+""".toRegex(), " ").replace("""<!--.+?-->""", "").replace("""�""", "")
    }

}



