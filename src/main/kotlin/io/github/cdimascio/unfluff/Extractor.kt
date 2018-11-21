package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

private val REGEX_COPYRIGHT = """.*?©(\s*copyright)?([^,;:.|\r\n]+).*""".toRegex(RegexOption.IGNORE_CASE)

class Extractor(private val doc: Document, private val language: Language = Language.en) {

    init {

        // parse doc
        // get top node - topNode = extractor.calculateBestNode(doc, lng)
        // cleaned doc

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

    fun copyright(): String? {
        val candidates = doc.select("""
            p[class*='copyright'],
            div[class*='copyright'],
            span[class*='copyright'],
            li[class*='copyright'],
            p[id*='copyright'],
            div[id*='copyright'],
            span[id*='copyright'],
            li[id*='copyright']
            """.trimIndent())
        for (c in candidates) {
            val text = c.text()
            if (text.isNotBlank()) return text
        }
        val match = REGEX_COPYRIGHT.find(doc.body().html())
        return match?.groupValues?.get(2)?.cleanse()
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
            var fallback = doc.select("span[class*='author']")
            if (fallback.isEmpty()) fallback = doc.select("p[class*='author']")
            if (fallback.isEmpty()) fallback = doc.select("div[class*='author']")
            if (fallback.isEmpty()) fallback = doc.select("span[class*='byline']")
            if (fallback.isEmpty()) fallback = doc.select("p[class*='byline']")
            if (fallback.isEmpty()) fallback = doc.select("div[class*='byline']")
            if (fallback.isNotEmpty()) authors.add(fallback.text().cleanse())
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
        return rawTitle()
//        return cleanTitle(titleText, ["|", " - ", "»", ":"])
    }

    fun softTitle() {

    }

    fun text(): String? {
        return null
    }

    private fun rawTitle(): String? {

        var candidates = mutableListOf<Element> ()
        candidates.addAll(doc.select("""meta[property='og:title']""").toList())
        candidates.add(doc.selectFirst("h1[class*='title']"))
        candidates.add(doc.selectFirst("title"))
        // The first h1 or h2 is a useful fallback
        candidates.add(doc.selectFirst("h1"))
        candidates.add(doc.selectFirst("h2"))

        var text: String? = null
        for (c in candidates) {
            text = c.attr("content").cleanse()
            if (text.isBlank()) text = c.text().cleanse()
            if (text.isNotBlank()) return text
        }
        return text
    }

    fun favicon() {

    }

    fun description() {

    }

    fun keywords() {

    }

    fun lang() {

    }

    fun canonicalLink() {

    }

    fun tags() {

    }

    fun image() {

    }

    fun videos() {

    }

    fun links() {

    }

    fun ext() {

    }

    private fun cleanNull(text: String?) = text?.trim()?.replace("null", "") ?: ""
//    private fun cleanText(text: String?, cleanNull: Boolean = false): String {
//        val trimmed = text?.trim()
//        val t = if (cleanNull) trimmed?.replace("null", "") else trimmed
//        return t?.
//            replace("null", "")?.
//            replace("""[\r\n\t]""".toRegex(), " ")?.
//            replace("""\s\s+""".toRegex(), " ")?.
//            replace("""<!--.+?-->""", "")?.
//            replace("""�""", "")?.trim() ?: ""
//    }

    fun String.cleanse(): String {
        return this.trim().replace("null", "").replace("""[\r\n\t]""".toRegex(), " ").replace("""\s\s+""".toRegex(), " ").replace("""<!--.+?-->""", "").replace("""�""", "")
    }
}

