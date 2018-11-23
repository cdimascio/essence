package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private val REGEX_COPYRIGHT = """.*?©(\s*copyright)?([^,;:.|\r\n]+).*""".toRegex(RegexOption.IGNORE_CASE)

class Extractor(private val doc: Document, private val language: Language = Language.en) {
    private val stopWords: StopWords = StopWords.load(language)
    private val extractorHeuristics = ExtractorHeuristics(doc, stopWords)
    private val topNode = extractorHeuristics.calculateBestNode()
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

    fun text(): String {
        return topNode?.let {
            PostCleanup(doc, topNode, language, stopWords, extractorHeuristics).clean(topNode)
            formatter.format(topNode)
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


class PostCleanup(private val doc: Document, private val topNode: Element?, private val language: Language, private val stopWords: StopWords, private val heuristics: ExtractorHeuristics) {
    fun clean(element: Element?) {
        if (element == null) return

        val isParagraphOrAnchor = { node: Element ->
            listOf("p", "a").contains(node.tagName())
        }
        addSiblingsToTopNode(element)?.let { updatedElement ->
            for (child in updatedElement.children()) {
                if (isParagraphOrAnchor(child)) {
                    if (heuristics.hasHighLinkDensity(child) ||
                        heuristics.isTableOrListWithNoParagraphs(child) ||
                        !heuristics.isNodeThresholdMet(updatedElement, child)) {
                        if (child.hasParent()) {
                            child.remove()
                        }
                    }
                }
            }
        }
    }

    private fun addSiblingsToTopNode(targetNode: Element?): Element? {
        val baselineParagraphSiblingScore = getSiblingsScore()
        if (targetNode == null) return null

        val previousSiblings = TraversalHelpers.getAllPreviousSiblings(targetNode)
        previousSiblings.filter { it is Element }.forEach { sib: Node ->
            val siblingContent = getSiblingsContent(sib as Element, baselineParagraphSiblingScore)
            for (content in siblingContent) {
                if (content.isNotBlank()) {
                    targetNode.prependChild(TextNode(content))
                }
            }
        }
        return targetNode
    }

    private fun getSiblingsContent(node: Element, score: Double): List<String> {
        if (node.tagName() == "p" && node.text().isNotBlank()) {
            return listOf(node.text())
        }
        val candidateParagraphs = node.select("p")
        if (candidateParagraphs.isEmpty()) {
            return emptyList()
        }
        val contents = mutableListOf<String>()
        for (p in candidateParagraphs) {
            val text = p.text()
            if (text.isNotBlank()) {
                val stats = stopWords.statistics(text)
                val paragraphScore = stats.stopWords.size
                val siblingBaselineScore = 0.30
                val hasHighLinkDensity = heuristics.hasHighLinkDensity(p)
                val sibScore = score * siblingBaselineScore

                if (sibScore < paragraphScore && !hasHighLinkDensity) {
                    contents.add(text)
                }
            }
        }

        return contents
    }

    private fun getSiblingsScore(): Double {
        val base = 100000.0
        var paragraphsNum = 0;
        var paragraphsScore = 0.0
        if (topNode == null) return base

        val elementsToCheck = topNode.select("p")
        for (element in elementsToCheck) {
            val text = element.text()
            val stats = stopWords.statistics(text)
            val hasHighLinkDensity = heuristics.hasHighLinkDensity(element)
            if (stats.stopWords.size > 2 && !hasHighLinkDensity) {
                paragraphsNum += 1
                paragraphsScore += paragraphsScore / paragraphsNum
            }
        }
        if (paragraphsNum > 0) {
            return paragraphsScore / paragraphsNum
        }
        return base

    }
}

class ExtractorHeuristics(private val doc: Document, private val stopWords: StopWords) {
    fun calculateBestNode(): Element? {
        val nodesWithText = mutableListOf<Node>()
        val nodesToCheck = doc.select("p, pre, td")
        nodesToCheck.forEach { node ->
            val text = node.text()
            val wordStats = stopWords.statistics(text)
            val hasHighLinkDensity = this.hasHighLinkDensity(node)

            if (wordStats.stopWords.size > 2 && !hasHighLinkDensity) {
                nodesWithText.add(node)
            }
        }

        val numNodesWithText = nodesWithText.size
        var startingBoost = 1.0
        var negativeScoring = 0
        var bottomNegativescoreNodes = numNodesWithText * 0.25
        val parentNodes = mutableSetOf<Node>()

        for ((index, node) in nodesWithText.iterator().withIndex()) {
            var boostScore = 0.0
            if (isBoostable(node) && index > 0) {
                boostScore = (1.0 / startingBoost) * 50
                startingBoost += 1
            }
            if (numNodesWithText > 15 && ((numNodesWithText - 1) <= bottomNegativescoreNodes)) {
                val booster = bottomNegativescoreNodes - (numNodesWithText - 1)
                boostScore = -1.0 * Math.pow(booster, 2.0)
                val negScore = Math.abs(boostScore) + negativeScoring

                if (negScore > 40) {
                    boostScore = 5.0
                }
            }

            // Give the current node a score of how many common words
            // it contains plus any boost
            val text = when (node) {
                is Element -> node.text()
                is TextNode -> node.text()
                else -> ""
            }

            // Give the current node a score of how many common words
            // it contains plus any boost
            val wordStats = stopWords.statistics(text)
            val upscore = Math.floor(wordStats.stopWords.size + boostScore)

            // THis only goes up 2 levels per node?
            // Propagate the score upwards
            val parent = node.parent()
            updateScore(parent, upscore)
            updateNodeCount(parent, 1)

            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent)
            }
            val grandParent: Node? = parent.parent()

            grandParent?.let {
                updateScore(it, upscore / 2.0)
                updateNodeCount(it, 1)
                if (!parentNodes.contains(grandParent)) {
                    parentNodes.add(grandParent)
                }
            }
        }

        var topNodeScore = 0.0
        var topNode: Element? = null
        // walk each parent and grandparent and find the one that contains the highest sum score
        // of 'texty' child nodes.
        // That's probably our best node!
        for (node in parentNodes) {
            if (node is Element) {
                // TODO only care about Element nodes?
                val score = getScore(node)
                if (score > topNodeScore) {
                    topNodeScore = score
                    topNode = node
                }
                if (topNode == null) {
                    topNode = node
                }
            } else {
                println("not an element, hence not considered for top node -ok?")
            }
        }
        return topNode
    }

    private fun getScore(node: Node): Double {
        return try {
            node.attr("gravityScore").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    private fun updateScore(node: Node, addToScore: Double) {
        val currentScore = getScore(node)
        val score = currentScore + addToScore
        node.attr("gravityScore", score.toString())
    }

    private fun updateNodeCount(node: Node, addToCount: Int) {
        val currentCount = try {
            node.attr("gravityNodes").toInt()
        } catch (e: Exception) {
            0
        }
        val count = currentCount + addToCount
        node.attr("gravityNodes", count.toString())
    }

    fun hasHighLinkDensity(node: Node): Boolean {
        if (node is Element) {
            val links = node.select("a")
            if (links.isNotEmpty()) {
                val linkText = links.map {
                    it.text()
                }.joinToString(" ")

                val words = node.text().split(" ")
                val linkWords = linkText.split(' ')


                val percentLinkWords = linkWords.size / words.size
                val score = percentLinkWords * links.size
                println("linkWord/words ${linkWords.size} / ${words.size} = $percentLinkWords | $percentLinkWords * ${links.size} = $score")
                return score >= 1.0
            }
        }
        return false
    }

    /**
     *  Given a text node, check all previous siblings.
     *  If the sibling node looks 'texty' and isn't too many
     *  nodes away, it's probably some yummy text
     **/
    fun isBoostable(node: Node): Boolean {
        val previousSiblings = TraversalHelpers.getAllPreviousElementSiblings(node)
        val MinimumStopwordCount = 5
        val MaxStepsawayFromNode = 3
        for ((stepsAway, element) in previousSiblings.iterator().withIndex()) {
            if (element.tagName() == "p") {
                if (stepsAway >= MaxStepsawayFromNode) {
                    return false
                }
                val paraText = element.text()
                val stats = stopWords.statistics(paraText)
                if (stats.stopWords.size > MinimumStopwordCount) {
                    return true
                }
            }
        }
        return false
    }

    fun isTableOrListWithNoParagraphs(element: Element): Boolean {
        val paragraphs = element.select("p")
        val remainingParagraphs = mutableListOf<Element>()
        for (p in paragraphs) {
            val text = p.text()
            if (text.isNotBlank() && text.length < 25) {
                p.remove()
            } else {
                remainingParagraphs.add(p)
            }
        }
        val isTableOrListWithNoParagraphs = remainingParagraphs.isNotEmpty() && listOf("td", "ul", "ol").contains(remainingParagraphs[0].tagName())
        return isTableOrListWithNoParagraphs
    }

    fun isNodeThresholdMet(parent: Node, child: Element): Boolean {
        val parentNodeScore = getScore(parent)
        val childNodeScore = getScore(child)
        val thresholdScore = parentNodeScore * 0.8

        val isAnExcludeTags = { tag: String -> listOf("td", "ul", "ol", "blockquote").contains(tag) }
        if (childNodeScore < thresholdScore && !isAnExcludeTags(child.tagName())) {
            return false
        }
        return true
    }
}
