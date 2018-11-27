package io.github.cdimascio.essence.extractors

import io.github.cdimascio.essence.formatters.Formatter
import org.jsoup.nodes.Element

internal object TextExtractor {
    fun extract(node: Element?, formatter: Formatter): String {
        return node?.let {
            formatter.format(node)
        } ?: ""
    }
}
