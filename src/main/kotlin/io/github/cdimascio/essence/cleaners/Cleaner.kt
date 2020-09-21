package io.github.cdimascio.essence.cleaners

import io.github.cdimascio.essence.cleaners.rules.Rule
import io.github.cdimascio.essence.util.find
import io.github.cdimascio.essence.util.matchFirstElementTags
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeFilter

private const val GRAVITY_USED_ALREADY = "grv-usedalready"

class Cleaner(private val doc: Document) {
    fun clean(): Document {
        removeBodyClasses()
        cleanEmTags()
        cleanCodeBlocks()
        removeDropCaps()
        removeScriptsStyles()
        Traverse(
            nodeRemovalRules = listOf(
//                Rule::removeNonTextNodes,
                Rule::removeCommentsTravRule,
                Rule::removeBadTagsTravRule,
                Rule::removeNavigationElements,
                Rule::removeSponsoredContent,
                (Rule::removeMatching)("""^caption$""".toRegex()),
                (Rule::removeMatching)(""" google """.toRegex()),
                (Rule::removeMatching)("""^[^entry-]more.*$""".toRegex()),
                (Rule::removeMatching)("""(["#.'\-_]+|^)(fb|facebook)[\-_"']+|facebook-broadcasting""".toRegex()),
                (Rule::removeMatching)("""[^-]twitter""".toRegex())),
            nodeModificationRules = listOf(
                Rule::correctErrantLineBreaks,
                Rule::cleanArticleTag
            ))
            .applyRules(doc)
        cleanParaSpans()
        cleanUnderlines()
        elementToParagraph(doc, listOf("div", "span"))

        return doc
    }

    /**
     * Remove all classes from body
     */
    private fun removeBodyClasses() {
        val body = doc.body()
        body.classNames().forEach {
            body.removeClass(it)
        }
    }

    private fun cleanEmTags() {
        val ems = doc.getElementsByTag("em")
        ems.forEach {
            val images = it.find("img")
            if (images.isEmpty()) {
                it.unwrap()
            }
        }
    }

    private fun cleanCodeBlocks() {
        val nodes = doc.select(
            "[class*='highlight-'], pre code, code, pre, ul.task-list"
        )
        nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeDropCaps() {
        val nodes = doc.select("span[class~=dropcap], span[class~=drop_cap]")
        return nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeScriptsStyles() {
        doc.getElementsByTag("script").remove()
        doc.getElementsByTag("style").remove()
    }

    private fun cleanParaSpans() {
        doc.select("p span").forEach {
            it.unwrap()
        }
    }

    private fun cleanUnderlines() {
        doc.select("u").forEach {
            it.unwrap()
        }
    }

    private fun elementToParagraph(doc: Document, tagNames: List<String>) {
        val elements = doc.select(tagNames.joinToString(",")) //\.reversed()
        val tags = listOf("a", "blockquote", "dl", "div", "img", "ol", "p", "pre", "table", "ul")
        for (element in elements) {
            val items = element.matchFirstElementTags(tags, 1)
            if (items.isEmpty()) {
                val html = element.html()
                element.tagName("p")
                element.html(html)

            } else {
                val replaceNodes = getReplacementNodes(element)
                val pReplacementElements = mutableListOf<Element>()
                for (rNode in replaceNodes) {
                    if (rNode.html().isNotEmpty()) {
                        pReplacementElements.add(Element("p").html(rNode.html()))
                    }
                }
                element.parent().insertChildren(element.siblingIndex(), pReplacementElements)
                element.remove()
            }
        }
    }

    private fun getReplacementNodes(div: Node): List<Element> {
        val children = div.childNodes()
        val nodesToReturn = mutableListOf<Element>()
        val nodesToRemove = mutableListOf<Node>()
        val replacmentText = mutableListOf<String>() // TODO: could be string buffer
        val isGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY) == "yes" }
        val setGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY, "yes") }
        for (kid in children) {
            if (kid is Element && kid.tagName() == "p" && replacmentText.isNotEmpty()) {
                val html = replacmentText.joinToString("")
                nodesToReturn.add(Element("p").html(html))
                replacmentText.clear()
                nodesToReturn.add(kid)
            } else if (kid is TextNode) {
                val kidText = kid.text()
                    .replace("""\n""", "\n\n")
                    .replace("""\t""", "")
                    .replace("""^\s+$""", "")
                if (kidText.length > 1) {
                    var prevSibling = kid.previousSibling()
                    while (prevSibling is Element && prevSibling.tagName() == "a" && !isGravityUsed(prevSibling)) {
                        val outerHtml = " ${prevSibling.outerHtml()} "
                        replacmentText.add(outerHtml)
                        nodesToRemove.add(prevSibling)
                        setGravityUsed(prevSibling)
                        prevSibling = prevSibling.previousSibling()
                    }

                    replacmentText.add(kidText)

                    var nextSibling = kid.nextSibling()
                    while (nextSibling is Element && nextSibling.tagName() == "a" && !isGravityUsed(nextSibling)) {
                        val outerHtml = " ${nextSibling.outerHtml()} "
                        replacmentText.add(outerHtml)
                        nodesToRemove.add(nextSibling)
                        setGravityUsed(nextSibling)
                        nextSibling = nextSibling.nextSibling()
                    }
                }
            } else {
                if (kid is Element) {
                    nodesToReturn += kid
                }
            }
        }

        if (replacmentText.isNotEmpty()) {
            val html = replacmentText.joinToString("")
            nodesToReturn.add(Element("p").html(html))
            replacmentText.clear()
        }

        for (node in nodesToRemove) {
            node.remove()
        }

        val isInteresting = { e: Element ->
            !listOf("meta", "head").contains(e.tagName())
        }
        return nodesToReturn.filter { isInteresting(it) }
    }
}

class Traverse(
    private val nodeRemovalRules: List<(Node) -> Boolean>,
    private val nodeModificationRules: List<(Node) -> Unit>) {

    fun applyRules(node: Node): Traverse {
        node.filter(object : NodeFilter {
            override fun tail(node: Node, depth: Int): NodeFilter.FilterResult {
                return NodeFilter.FilterResult.CONTINUE
            }

            override fun head(node: Node, depth: Int): NodeFilter.FilterResult {
                for (rule in nodeModificationRules) {
                    rule(node)
                }

                for (rule in nodeRemovalRules) {
                    if (rule(node)) {
                        return NodeFilter.FilterResult.REMOVE
                    }
                }
                return NodeFilter.FilterResult.CONTINUE
            }
        })
        return this
    }
}

