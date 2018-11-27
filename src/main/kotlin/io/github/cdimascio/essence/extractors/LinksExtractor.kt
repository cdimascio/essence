package io.github.cdimascio.essence.extractors

import io.github.cdimascio.essence.Link
import io.github.cdimascio.essence.util.find
import org.jsoup.nodes.Element

internal object LinksExtractor {
    fun extract(node: Element?): List<Link> {
        val gatherLinks = { node: Element ->
            node.find("a").fold(mutableListOf<Link>()) { links, node ->
                val href = node.attr("href").cleanse()
                val text = node.text().cleanse() // or html
                if (href.isNotBlank() && text.isNotBlank()) {
                    links += Link(href, text)
                }
                links
            }
        }

        return node?.let { gatherLinks(node) } ?: emptyList()
    }
}

