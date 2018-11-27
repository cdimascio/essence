package io.github.cdimascio.essence.util

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.Elements
import org.jsoup.select.NodeFilter

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

fun Element.find(selector: String): Elements {
    val results = this.select(selector)
    results.remove(this)
    return results
}

fun Element.matchFirstElementTags(elementTags: List<String>, n: Int): Elements {
    val elements = Elements()
    var count = 0
    this.filter(object : NodeFilter {
        override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
            return NodeFilter.FilterResult.CONTINUE
        }

        override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
            if (node is Element && elementTags.contains(node.tagName())) {
                elements.add(node)
                count += 1
            }
            return if (count == n) NodeFilter.FilterResult.STOP
            else NodeFilter.FilterResult.CONTINUE
        }

    })
    return elements
}
