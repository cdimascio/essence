package io.github.cdimascio.unfluff

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

object TraversalHelpers {
    fun getAllPreviousElementSiblings(node: Node): List<Element> {
        val previousSiblings = mutableListOf<Element>()
        var previousSibling = node.previousSibling()
        while (previousSibling != null) {
            if (previousSibling is Element) {
                previousSiblings.add(previousSibling)
            }
            previousSibling = previousSibling.previousSibling()
        }
        return previousSiblings
    }

    fun getAllPreviousSiblings(node: Node): List<Node> {
        val previousSiblings = mutableListOf<Node>()
        var previousSibling = node.previousSibling()
        while (previousSibling != null) {
            previousSiblings.add(previousSibling)
            previousSibling = previousSibling.previousSibling()
        }
        return previousSiblings
    }
}
