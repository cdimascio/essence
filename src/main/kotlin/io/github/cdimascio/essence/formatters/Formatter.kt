package io.github.cdimascio.essence.formatters

import org.jsoup.nodes.Element

interface Formatter {
    fun format(node: Element?): String
}
