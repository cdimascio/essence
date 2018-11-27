package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class DocumentScorer(private val doc: Document, private val stopWords: StopWords) {
    private var scored = false
    private val heuristics = Heuristics(this, stopWords)
    private val filterByScorer = FilterScoredNodes(stopWords, heuristics)

    fun score(): Element? {
        val nodesWithText = mutableListOf<Element>()
        val nodesToCheck = doc.select("p, pre, td")
        nodesToCheck.forEach { node ->
            val text = node.text()
            val wordStats = stopWords.statistics(text)
            val hasHighLinkDensity = heuristics.hasHighLinkDensity(node)

            if (wordStats.stopWords.size > 2 && !hasHighLinkDensity) {
                nodesWithText.add(node)
            }
        }

        val numNodesWithText = nodesWithText.size
        var startingBoost = 1.0
        val negativeScoring = 0
        val bottomNegativescoreNodes = numNodesWithText * 0.25
        val parentNodes = mutableSetOf<Element>()

        for ((index, node) in nodesWithText.iterator().withIndex()) {
            var boostScore = 0.0
            // If this node has nearby nodes that contain
            // some good text, give the node some boost points
            if (isBoostable(node)) { // && index >= 0) {
                boostScore = (1.0 / startingBoost) * 50
                startingBoost += 1
            }
            if (numNodesWithText > 15 && ((numNodesWithText - index) <= bottomNegativescoreNodes)) {
                val booster = bottomNegativescoreNodes - (numNodesWithText - 1)
                boostScore = -1.0 * Math.pow(booster, 2.0)
                val negScore = Math.abs(boostScore) + negativeScoring

                if (negScore > 40) {
                    boostScore = 5.0
                }
            }

            // Give the current node a score of how many common words
            // it contains plus any boost
            val text = node.text()
            // Give the current node a score of how many common words
            // it contains plus any boost
            val wordStats = stopWords.statistics(text)
            val upScore = Math.floor(wordStats.stopWords.size + boostScore)

            // THis only goes up 2 levels per node?
            // Propagate the score upwards
            val parent = node.parent()
            updateScore(parent, upScore)
            updateNodeCount(parent, 1)

            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent)
            }
            val grandParent: Node? = parent.parent()

            grandParent?.let {
                updateScore(it, upScore / 2.0)
                updateNodeCount(it, 1)
                if (!parentNodes.contains(grandParent)) {
                    if (grandParent is Element) {
                        parentNodes.add(grandParent)
                    } else {
                        throw java.lang.IllegalStateException("Fix unfluffer code: Didn't think a parent could be anything but an element here")
                    }
                }
            }
        }

        scored = true
        val topNode = identifyTopNode(parentNodes)
        filterByScorer.clean(topNode)
        return topNode
    }

    private fun identifyTopNode(scoredNodes: Set<Element>): Element? {
        if (!scored) throw IllegalStateException("score() must be called prior to calling getTopNode")
        var topNodeScore = 0.0
        var topNode: Element? = null
        // walk each parent and grandparent and find the one that contains the highest sum score
        // of 'texty' child nodes.
        // That's probably our best node!
        for (node in scoredNodes) {
            val score = getScore(node)
            if (score > topNodeScore) {
                topNodeScore = score
                topNode = node
            }
            if (topNode == null) {
                topNode = node
            }
        }
        return topNode
    }

    internal fun getScore(node: Node): Double {
        return try {
            node.attr("gravityScore").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    // TODO: why update the DOM to track score - do seomething else
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

    /**
     *  Given a text node, check all previous siblings.
     *  If the sibling node looks 'texty' and isn't too many
     *  nodes away, it's probably some yummy text
     **/
    private fun isBoostable(node: Node): Boolean {
        val previousSiblings = TraversalHelpers.getAllPreviousElementSiblings(node)
        val MIN_STOP_WORDS_COUNT = 5
        val MAX_STEPS_FROM_NODE = 3
        for ((stepsAway, element) in previousSiblings.iterator().withIndex()) {
            if (element.tagName() == "p") {
                if (stepsAway >= MAX_STEPS_FROM_NODE) {
                    return false
                }
                val text = element.text()
                val stats = stopWords.statistics(text)
                if (stats.stopWords.size > MIN_STOP_WORDS_COUNT) {
                    return true
                }
            }
        }
        return false
    }
}

class Heuristics(private val scorer: DocumentScorer, private val stopWords: StopWords) {
    fun isTableOrListWithNoParagraphs(element: Element): Boolean {
        val paragraphs = element.find("p")
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
        val parentNodeScore = scorer.getScore(parent)
        val childNodeScore = scorer.getScore(child)
        val thresholdScore = parentNodeScore * 0.08

        val isAnExcludeTags = { tag: String -> listOf("td", "ul", "ol", "blockquote").contains(tag) }
        if (childNodeScore < thresholdScore && !isAnExcludeTags(child.tagName())) {
            return false
        }
        return true
    }

    fun hasHighLinkDensity(node: Node): Boolean {
        if (node is Element) {
            val text = node.text()
            val words = if (text.isNotBlank()) text.split(" ") else emptyList()
            val links = node.find("a")
            if (words.isEmpty() && links.isNotEmpty()) {
                return true
            } else if (links.isNotEmpty()) {
                val linkText = links.map{ it.text() }.joinToString(" ")
                val linkWords = linkText.split(" ")

                val percentLinkWords = linkWords.size.toDouble() / words.size.toDouble()
                val score = percentLinkWords * links.size

                return score >= 1.0
            }
        }
        return false
    }
}

private class FilterScoredNodes(private val stopWords: StopWords, private val heuristics: Heuristics) {
    fun clean(element: Element?) {
        if (element == null) return

        val isParagraphOrAnchor = { node: Element ->
            listOf("p", "a").contains(node.tagName())
        }
        addSiblingsToTopNode(element)?.let { updatedElement ->
            for (child in updatedElement.children()) {
                if (!isParagraphOrAnchor(child)) {
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

    // Why add only previous siblings -- change name of function
    private fun addSiblingsToTopNode(targetNode: Element?): Element? {
        val baselineParagraphSiblingScore = getSiblingsScore(targetNode)
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
        val candidateParagraphs = node.find("p")
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

    private fun getSiblingsScore(topNode: Element?): Double {
        val base = 100000.0
        var paragraphsNum = 0;
        var paragraphsScore = 0.0
        if (topNode == null) return base

        val elementsToCheck = topNode.find("p")
        for (element in elementsToCheck) {
            val text = element.text()
            val stats = stopWords.statistics(text)
            val hasHighLinkDensity = heuristics.hasHighLinkDensity(element)
            if (stats.stopWords.size > 2 && !hasHighLinkDensity) {
                paragraphsNum += 1
                paragraphsScore += stats.stopWords.size
            }
        }
        if (paragraphsNum > 0) {
            return paragraphsScore / paragraphsNum
        }
        return base
    }
}

