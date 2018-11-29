package io.github.cdimascio.essence.util

import io.github.cdimascio.essence.scorers.Scorer
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

object NodeHeuristics {
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
        return remainingParagraphs.isNotEmpty() &&
            listOf("td", "ul", "ol")
                .contains(remainingParagraphs[0].tagName())
    }

    fun isNodeThresholdMet(parent: Node, child: Element): Boolean {
        val parentNodeScore = Scorer.getScore(parent)
        val childNodeScore = Scorer.getScore(child)
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
                val percentLinkWords = links.size.toDouble() / words.size.toDouble()
                val score = percentLinkWords * links.size

                return score >= 1.0
            }
        }
        return false
    }
}
