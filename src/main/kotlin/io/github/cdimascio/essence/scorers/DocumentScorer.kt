package io.github.cdimascio.essence.scorers

import io.github.cdimascio.essence.util.NodeHeuristics
import io.github.cdimascio.essence.util.TraversalHelpers
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

class DocumentScorer(private val stopWords: StopWords) : Scorer {

    override fun score(doc: Document): ScoredElement? {
        val nodesWithText = mutableListOf<Element>()
        val nodesToCheck = doc.select("p, pre, td")
        nodesToCheck.forEach { node ->
            val text = node.text()
            val wordStats = stopWords.statistics(text)
            val hasHighLinkDensity = NodeHeuristics.hasHighLinkDensity(node)

            if (wordStats.stopWords.size > 2 && !hasHighLinkDensity) {
                nodesWithText.add(node)
            }
        }
        val numNodesWithText = nodesWithText.size
        var startingBoost = 1.0
        val negativeScoring = 0
        val bottomNegativeScoreNodes = numNodesWithText * 0.25
        val parentNodes = mutableSetOf<Element>()

        for ((index, node) in nodesWithText.iterator().withIndex()) {
            var boostScore = 0.0
            // If this node has nearby nodes that contain
            // some good text, give the node some boost points
            if (isBoostable(node)) { // && index >= 0) {
                boostScore = (1.0 / startingBoost) * 50
                startingBoost += 1
            }
            if (numNodesWithText > 15 && ((numNodesWithText - index) <= bottomNegativeScoreNodes)) {
                val booster = bottomNegativeScoreNodes - (numNodesWithText - 1)
                boostScore = -1.0 * booster.pow(2.0)
                val negScore = abs(boostScore) + negativeScoring

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
            val upScore = floor(wordStats.stopWords.size + boostScore)

            // THis only goes up 2 levels per node?
            // Propagate the score upwards
            val parent = node.parent()
            Scorer.updateScore(parent, upScore)
            Scorer.updateNodeCount(parent, 1)

            if (!parentNodes.contains(parent)) {
                parentNodes.add(parent)
            }
            val grandParent: Node? = parent.parent()

            grandParent?.let {
                Scorer.updateScore(it, upScore / 2.0)
                Scorer.updateNodeCount(it, 1)
                if (!parentNodes.contains(grandParent)) {
                    if (grandParent is Element) {
                        parentNodes.add(grandParent)
                    } else {
                        throw java.lang.IllegalStateException("Fix unfluffer code: Didn't think a parent could be anything but an element here")
                    }
                }
            }
        }

        return findTopNode(parentNodes)

    }

    private fun findTopNode(scoredNodes: Set<Element>): Element? {
        var topNodeScore = 0.0
        var topNode: Element? = null
        // walk each parent and grandparent and find the one that contains the highest sum score
        // of 'texty' child nodes.
        // That's probably our best node!
        for (node in scoredNodes) {
            val score = Scorer.getScore(node)
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
