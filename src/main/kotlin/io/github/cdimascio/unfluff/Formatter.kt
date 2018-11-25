package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class Formatter(private val doc: Document, val language: Language, private val stopWords: StopWords) {
    fun format(node: Element): String {
        removeNegativescoresNodes(node)
        linksToText(node)
        addNewlineToBr( node)
        replaceWithText(node)
        removeFewwordsParagraphs(node)
        return convertToText(node)
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
            if ((tag != "br" || text != "\\r") && numStopWords < 3 && !hasObject && !hasEmbed) {
                println("removing $e (small paragraph 1)")
                if (e.parent() != null)
                    e.remove()
            } else {
                val trimmed = text.trim()
                val numWords = text.split(" ").size
                if (trimmed.isNotBlank() && numWords < 8 && trimmed.first() == '(' && trimmed.last() == ')') {
                    println("removing $e (small paragraph 2)")
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
        val hangingText = StringBuffer() //"" // should be stringbuffer
        for (child in node.childNodes()) {
            if (child is TextNode) {
                hangingText.append(child.text())
                continue
            } else if (child is Element && child.tagName() == "ul") {
                hangingText.append(ulToText(child))
                continue
            }

            if (hangingText.isNotBlank()) {
                val text = cleanParagraphText(hangingText.toString())
                texts.addAll(text.split("""\r?\n""".toRegex()).map { it.trim() })
                hangingText.setLength(0)
            }
            val childText = when (child) {
                is TextNode -> child.text() // can't get here checked start of for loop
                is Element -> child.text()
                else -> ""
            }
            val text = cleanParagraphText(childText).replace("""(\\w+\\.)([A-Z]+)""".toRegex()) {
                val (group1, group2) = it.destructured
                "$group1 $group2"
            }
            texts.addAll(text.split("""\r?\n""".toRegex()).map { it.trim() })

            if (hangingText.isNotBlank()) {
                val text = cleanParagraphText(hangingText.toString())
                texts.addAll(text.split("""\r?\n""".toRegex()).map { it.trim() })
            }
        }

        // Make sure each text chunk includes at least one text character or number.
        // This supports multiple languages words using XRegExp to generate the
        // regex that matches ranges of unicode characters used in words.
        // TODO
        // apply filter on texts.filter { /*  ensure at least one character  is present */}
        return texts.joinToString("\n\n")
    }

    private fun ulToText(node: Element): String {
        val nodes = node.find("li")
        val text = nodes.fold("") { text, n ->
            text + "\n ${n.text()}"
        }
        return if (text.isNotBlank()) "\n $text" else ""
    }

    private fun cleanParagraphText(text: String): String {
        return text.trim().replace("""[\s\t]+""".toRegex(), " ")
    }
}
