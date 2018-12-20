package io.github.cdimascio.essence.formatters

import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class TextFormatter(private val stopWords: StopWords) : Formatter {

    override fun format(node: Element?) = node?.let {
        val bestRoot = drillDownToCruxElement(node)
        // TODO: Combine the following into a single pass
        removeNegativescoresNodes(bestRoot)
        superSubScriptToText(bestRoot)
        linksToText(bestRoot)
        addNewlineToBr(bestRoot)
        replaceWithText(bestRoot)
        removeFewwordsParagraphs(bestRoot)
        // TODO: find proper root
        // look look at children. if one node, look at their children to see if there are many
        // if there are many use that node as th root
        convertToText(bestRoot)
    } ?: ""

    private fun drillDownToCruxElement(node: Element): Element {
        if (node.ownText().isBlank() && node.childNodeSize() == 1) {
            val onlyChild = node.childNode(0)
            if (onlyChild is Element) {
                drillDownToCruxElement(onlyChild)
            }
        }
        return node
    }

    private fun removeNegativescoresNodes(node: Element) {
        val gravityElements = node.find("*[gravityScore]")
        gravityElements.forEach {
            val score = try {
                it.attr("gravityScore").toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }

            if (score < 0.0) {
                it.remove()
            }
        }
    }

    private fun superSubScriptToText(node: Element) {
        try {
            if (listOf("sub", "sup", "small").contains(node.tagName())) {
                node.ownText().trim().toDouble()
                if (node.hasParent()) {
                    node.unwrap()
                }
            }
        } catch (e: NumberFormatException) {
        }
    }

    private fun linksToText(node: Element) {
        val nodes = node.find("a")
        nodes.forEach {
            it.unwrap()
        }
    }

    private fun addNewlineToBr(node: Element) {
        val brs = node.find("br")
        brs.forEach {
            it.replaceWith(TextNode("\n\n"))
        }
    }

    private fun replaceWithText(node: Element) {
        val nodes = node.find("b, strong, i, br, sup")
        nodes.forEach {
            if (it.text().isNotBlank()) {
                it.unwrap()
            }
        }
    }

    private fun removeFewwordsParagraphs(node: Element) {
        val elements = node.find("*")
        for (e in elements) {
            val tag = e.tagName()
            val text = e.text()
            val numStopWords = stopWords.statistics(text).stopWords.size
            val hasObject = e.find("object").isNotEmpty()
            val hasEmbed = e.find("embed").isNotEmpty()
            val isEndline = tag == "br" || text == "\\r"
            if (!isEndline && numStopWords < 3 && !hasObject && !hasEmbed) {
                if (e.parent() != null)
                    e.remove()
            } else {
                val trimmed = text.trim()
                val numWords = text.split(" ").size
                if (trimmed.isNotBlank() && numWords < 25 && trimmed.first() == '(' && trimmed.last() == ')') {
                    // remove paragraph's that are surrounded entirely by parens and have few-ish words
                    if (e.parent() != null)
                        e.remove()
                }
            }
        }
    }

    private fun convertToText(node: Node): String {
        // To hold any text fragments that end up in text nodes outside of
        // html elements
        val texts = mutableListOf<String>()
        val hangingText = StringBuffer()
        for (child in node.childNodes()) {
            if (child is TextNode) {
                hangingText.append(child.text())
                continue
            } else if (child is Element && child.tagName() == "ul") {
                hangingText.append(ulToText(child))
                continue
            }

            // TODO if hanging text is blank here, we should reset the text to empty
            if (hangingText.isNotBlank()) {
                val text = cleanParagraphText(hangingText.toString())
                texts.addAll(text.split("""\r?\n""".toRegex()))
                hangingText.setLength(0)
            }
            if (child is Element) {
                val text = cleanParagraphText(child.text())
                    .replace("""(\\w+\\.)([A-Z]+)""".toRegex()) {
                        val (group1, group2) = it.destructured
                        "$group1 $group2"
                    }
                texts.addAll(text.split("""\r?\n""".toRegex()))

                if (hangingText.isNotBlank()) {
                    val text = cleanParagraphText(hangingText.toString())
                    texts.addAll(text.split("""\r?\n""".toRegex()))
                }
            }
        }

        // Make sure each text chunk includes at least one text character or number.
        // This supports multiple languages words using XRegExp to generate the
        // regex that matches ranges of unicode characters used in words.
        // TODO
        // apply filter on texts.filter { /*  ensure at least one character  is present */}
        return texts.map { it.trim() }.joinToString("\n\n")
    }

    private fun ulToText(node: Element): String {
        val nodes = node.find("li")
        val text = nodes.fold("") { text, n ->
            text + "\n * ${n.text()}"
        }
        return if (text.isNotBlank()) "\n $text" else ""
    }

    private fun cleanParagraphText(text: String): String {
        return text.trim()
            // replace 1 or more tabs and spaces with 1 space
            .replace("""[^\S\n]+""".toRegex(), " ")
            // replace 3 or more \n with 2 \n
            .replace("""[\n{3,]""", "\n\n")
    }
}
