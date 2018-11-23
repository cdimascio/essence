package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.lang.IllegalStateException

class DocumentScorer(private val doc: Document, private val language: Language, private val stopWords: StopWords) {
    private val scoredNodes = mutableSetOf<Node>()
    private var scored = false
    private val heuristics = Heuristics(this, stopWords)
    private val filterByScorer = FilterScoredNodes(language, stopWords, heuristics)

    fun score(): Element? {
        val nodesWithText = mutableListOf<Node>()
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

        scoredNodes.addAll(parentNodes)
        scored = true
        val topNode = identifyTopNode()
        filterByScorer.clean(topNode, this)
        return topNode
    }

    private fun identifyTopNode(): Element? {
        if (!scored) throw IllegalStateException("score() must be called prior to calling getTopNode")
        var topNodeScore = 0.0
        var topNode: Element? = null
        // walk each parent and grandparent and find the one that contains the highest sum score
        // of 'texty' child nodes.
        // That's probably our best node!
        for (node in scoredNodes) {
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

    internal fun getScore(node: Node): Double {
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

    /**
     *  Given a text node, check all previous siblings.
     *  If the sibling node looks 'texty' and isn't too many
     *  nodes away, it's probably some yummy text
     **/
    private fun isBoostable(node: Node): Boolean {
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
}

class Heuristics(private val scorer: DocumentScorer, private val stopWords: StopWords) {
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
        val parentNodeScore = scorer.getScore(parent)
        val childNodeScore = scorer.getScore(child)
        val thresholdScore = parentNodeScore * 0.8

        val isAnExcludeTags = { tag: String -> listOf("td", "ul", "ol", "blockquote").contains(tag) }
        if (childNodeScore < thresholdScore && !isAnExcludeTags(child.tagName())) {
            return false
        }
        return true
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
}

private class FilterScoredNodes(private val language: Language, private val stopWords: StopWords, private val heuristics: Heuristics) {
    fun clean(element: Element?, scorer: DocumentScorer) {
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

    private fun getSiblingsScore(topNode: Element?): Double {
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

