package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private val REGEX_BAD_TAGS = """^side$|combx|retweet|mediaarticlerelated|menucontainer|navbar|partner-gravity-ad|video-full-transcript|storytopbar-bucket|utility-bar|inline-share-tools|comment|PopularQuestions|contact|foot|footer|Footer|footnote|cnn_strycaptiontxt|cnn_html_slideshow|cnn_strylftcntnt|links|meta$|shoutbox|sponsor|tags|socialnetworking|socialNetworking|cnnStryHghLght|cnn_stryspcvbx|^inset$|pagetools|post-attributes|welcome_form|contentTools2|the_answers|communitypromo|runaroundLeft|subscribe|vcard|articleheadings|date|^print$|popup|author-dropdown|tools|socialtools|byline|konafilter|KonaFilter|breadcrumbs|^fn$|wp-caption-text|legende|ajoutVideo|timestamp|js_replies""".toRegex(RegexOption.IGNORE_CASE)
private val REGEX_NAV = """["#.'-_]+nav[-_"']+""".toRegex(RegexOption.IGNORE_CASE)
private val GRAVITY_USED_ALREADY = "grv-usedalready"

class Cleaner(private val doc: Document) {
    fun clean(): CleanDocument {
        removeBodyClasses()
        cleanArticleTags()
        cleanEmTags()
        cleanCodeBlocks()
        removeDropCaps()
        removeScriptsStyles()
        Traverse(
            nodeRemovalRules = listOf(
                TraversalRules::removeCommentsTravRule,
                TraversalRules::removeBadTagsTravRule,
                TraversalRules::removeNavigationElements,
                (TraversalRules::removeMatching)("""^caption$""".toRegex()),
                (TraversalRules::removeMatching)(""" google """.toRegex()),
                (TraversalRules::removeMatching)("""^[^entry-]more.*$""".toRegex()),
                (TraversalRules::removeMatching)("""[^-]facebook""".toRegex()),
                (TraversalRules::removeMatching)("""facebook-broadcasting""".toRegex()),
                (TraversalRules::removeMatching)("""[^-]twitter""".toRegex())),
            nodeModificationRules = listOf(
                TraversalRules::correctErrantLineBreaks
            ))
            .applyRules(doc)
            .purgeMarkedNodes()
        cleanParaSpans()
        cleanUnderlines()

        elementToParagraph(doc, listOf("div", "span"))

        return CleanDocument(
            text = doc.html(),
            doc = doc
        )
    }

    /**
     * Remove all classes
     */
    private fun removeBodyClasses() {
        val body = doc.body()
        body.classNames().forEach {
            body.removeClass(it)
        }
    }

    private fun cleanArticleTags() {
        val articles = doc.getElementsByTag("article")
        articles.forEach {
            it.removeAttr("id")
            it.removeAttr("name")
            it.removeAttr("class")
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

    var count = 0
    var e = 0
    private fun elementToParagraph(doc: Document, tagNames: List<String>) {
        val elements = doc.select(tagNames.joinToString(","))
        val tags = listOf("a", "blockquote", "dl", "div", "img", "ol", "p", "pre", "table", "ul")
        println("divsToPara ${elements.size}")
        for (element in elements) {
            // TODO: can we find the first that isn't this element --- this is a performance issue as is!
            val items = element.find(tags.joinToString(", "))
            if (items.isEmpty()) {
                println("${count++} '${element.html()}' ")
                val html = element.html()
                element.tagName("p")
                element.html(html)

            } else {
                val replaceNodes = getReplacementNodes(element)
                println("replace nodes ${replaceNodes.size}")
                val pReplacementElements = mutableListOf<Element>()
                for (rNode in replaceNodes) {
                    if (rNode is TextNode && rNode.text().isNotEmpty()) {
                        println("-" + e++)
                        pReplacementElements.add(Element("p").html(rNode.text()))
                    } else if (rNode is Element) {
                        if (rNode.html().isNotEmpty()) {
                            println("-" + e++)
                            pReplacementElements.add(Element("p").html(rNode.html()))
                        }
                    } else {
                        println("ERROR - should not get here")
                    }
                }
                element.parent().insertChildren(element.siblingIndex(), pReplacementElements)
                element.remove()
            }
        }
    }

    private fun getReplacementNodes(div: Node): List<Node> {
        val children = div.childNodes()
        val nodesToReturn = mutableListOf<Node>()
        val nodesToRemove = mutableListOf<Node>()
        // really replacement HTML
        val replacmentText = mutableListOf<String>() // could be string buffer
        val isGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY) == "yes" }
        val setGravityUsed = { e: Element -> e.attr(GRAVITY_USED_ALREADY, "yes") }
        for (kid in children) {
            if (kid is Element && kid.tagName() == "p" && replacmentText.isNotEmpty()) {
                val html = replacmentText.joinToString("")
                println("p with replacement $html")
                nodesToReturn.add(Element("p").html(html))
                replacmentText.clear()
                nodesToReturn.add(kid)
            } else if (kid is TextNode) {
                val kidText = kid.text()
                    .replace("""\n""", "\n\n")
                    .replace("""\t""", "")
                    .replace("""^\s+$""", "")
                if (kidText.length > 1) {
                    println("text with replace text")
                    var prevSibling = kid.previousSibling()
                    while (prevSibling is Element && prevSibling.tagName() == "a" && !isGravityUsed(prevSibling)) {
                        println("prev sib handling")
                        val outerHtml = " ${prevSibling.outerHtml()} "
                        replacmentText.add(outerHtml)
                        nodesToRemove.add(prevSibling)
                        setGravityUsed(prevSibling)
                        prevSibling = prevSibling.previousSibling()
                    }

                    replacmentText.add(kidText)

                    var nextSibling = kid.nextSibling()
                    while (nextSibling is Element && nextSibling.tagName() == "a" && !isGravityUsed(nextSibling)) {
                        println("next sib handling")
                        val outerHtml = " ${nextSibling.outerHtml()} "
                        replacmentText.add(outerHtml)
                        nodesToRemove.add(nextSibling)
                        setGravityUsed(nextSibling)
                        nextSibling = nextSibling.nextSibling()
                    }
                }
            } else {
                nodesToReturn.add(kid)
            }
        }

        if (replacmentText.isNotEmpty()) {
            println("finish replacement text")
            val html = replacmentText.joinToString("")
            nodesToReturn.add(Element("p").html(html))
            replacmentText.clear()
        }

        for (node in nodesToRemove) {
            node.remove()
        }

        return nodesToReturn
    }
}

class Traverse(val nodeRemovalRules: List<(Node) -> Boolean>, val nodeModificationRules: List<(Node) -> Unit>) {
    private val nodesToRemove = mutableSetOf<Node>()
    fun applyRules(node: Node): Traverse {
        for (child in node.childNodes()) {
            var nodeMarkedForRemoval = false
            for (rule in nodeRemovalRules) {
                if (rule(child)) {
                    nodeMarkedForRemoval = true
                    nodesToRemove += child
                    break
                }
            }
            if (nodeMarkedForRemoval) continue

            for (rule in nodeModificationRules) {
                rule(child)
            }
            applyRules(child)
        }
        return this
    }

    /**
     * purge nodes marked for deletion
     */
    fun purgeMarkedNodes() {
        nodesToRemove.forEach {
            it.remove()
        }
    }
}

object TraversalRules {
    /**
     * Remove node if a comment nodes and return true, else return false
     */
    fun removeCommentsTravRule(node: Node): Boolean {
        return node.nodeName() == "#comment"
    }

    fun removeNavigationElements(node: Node): Boolean {
        if (node !is Element) return false
        return listOf("div", "li", "ul", "ol").contains(node.tagName()) && (
            REGEX_NAV.containsMatchIn(node.attr("class")) ||
                REGEX_NAV.containsMatchIn(node.attr("id")))
    }


    fun removeBadTagsTravRule(node: Node) =
        REGEX_BAD_TAGS.containsMatchIn(node.attr("id")) ||
            REGEX_BAD_TAGS.containsMatchIn(node.attr("class")) ||
            REGEX_BAD_TAGS.containsMatchIn(node.attr("name"))

    fun removeMatching(re: Regex): (Node) -> Boolean {
        return { node: Node ->
            (node is Element && node.tagName() == "div") &&
                (re.containsMatchIn(node.attr("id")) ||
                    re.containsMatchIn(node.attr("class")))
        }
    }

    fun correctErrantLineBreaks(node: Node) {
        if (node is Element && node.tagName() == "p") {
            for (textNode in node.textNodes()) {
                val text = textNode.text().replace("""([^\n])\n([^\n])""".toRegex()) {
                    it.groupValues.joinToString(" ")
                }
                textNode.text(text)
            }
        }
    }
}
