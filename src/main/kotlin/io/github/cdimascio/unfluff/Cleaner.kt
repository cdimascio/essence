package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

val REGEX_BAD_TAGS = """^side$|combx|retweet|mediaarticlerelated|menucontainer|navbar|partner-gravity-ad|video-full-transcript|storytopbar-bucket|utility-bar|inline-share-tools|comment|PopularQuestions|contact|foot|footer|Footer|footnote|cnn_strycaptiontxt|cnn_html_slideshow|cnn_strylftcntnt|links|meta$|shoutbox|sponsor|tags|socialnetworking|socialNetworking|cnnStryHghLght|cnn_stryspcvbx|^inset$|pagetools|post-attributes|welcome_form|contentTools2|the_answers|communitypromo|runaroundLeft|subscribe|vcard|articleheadings|date|^print$|popup|author-dropdown|tools|socialtools|byline|konafilter|KonaFilter|breadcrumbs|^fn$|wp-caption-text|legende|ajoutVideo|timestamp|js_replies""".toRegex(RegexOption.IGNORE_CASE)

class Cleaner(private val doc: Document, private val language: Language) {
    private var d = doc
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
                (TraversalRules::removeMatching)("""^caption$""".toRegex()),
                (TraversalRules::removeMatching)(""" google """.toRegex()),
                (TraversalRules::removeMatching)("""^[^entry-]more.*$""".toRegex()),
                (TraversalRules::removeMatching)("""[^-]facebook""".toRegex()),
                (TraversalRules::removeMatching)("""facebook-broadcasting""".toRegex()),
                (TraversalRules::removeMatching)("""[^-]twitter""".toRegex())),
            nodeModificationRules = listOf(
                TraversalRules::correctErrantLineBreaks
            ))
            .applyRules(d)
            .purgeMarkedNodes()
        cleanParaSpans()
        cleanUnderlines()

        elementToParagraph(d, "div")

//        elementToParagraph(d, "span")

        return CleanDocument(
            text = d.html(),
            doc = d
        )
    }

    /**
     * Remove all classes
     */
    private fun removeBodyClasses() {
        val body = d.body()
        body.classNames().forEach {
            body.removeClass(it)
        }
    }

    private fun cleanArticleTags() {
        val articles = d.getElementsByTag("article")
        articles.forEach {
            it.removeAttr("id")
            it.removeAttr("name")
            it.removeAttr("class")
        }
    }

    private fun cleanEmTags() {
        val ems = d.getElementsByTag("em")
        ems.forEach {
            val images = it.find("img")
            if (images.isEmpty()) {
                it.unwrap()
            }
        }
    }

    private fun cleanCodeBlocks() {
        val nodes = d.select(
            "[class*='highlight-'], pre code, code, pre, ul.task-list"
        )
        nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeDropCaps() {
        val nodes = d.select("span[class~=dropcap], span[class~=drop_cap]")
        return nodes.forEach {
            it.unwrap()
        }
    }

    private fun removeScriptsStyles() {
        d.getElementsByTag("script").remove()
        d.getElementsByTag("style").remove()
    }

    private fun cleanParaSpans() {
        d.select("p span").forEach {
            it.unwrap()
        }
    }

    private fun cleanUnderlines() {
        d.select("u").forEach {
            it.unwrap()
        }
    }

    var count = 0
    private fun elementToParagraph(doc: Document, tagName: String) {
//        println (doc.html())
        val elements = doc.select(tagName)
        val tags = listOf("a", "blockquote", "dl", "div", "img", "ol", "p", "pre", "table", "ul")
        println("===divs ${elements.size}")
        println("p 1 ${doc.select("p").size}")
        for (element in elements) {
            // TODO: can we find the first that isn't this element --- this is a performance issue as is!
            val items = element.find(tags.joinToString(", "))
            if (items.isEmpty()) {
                count += 1
                val html = element.html()
                element.tagName("p")
                element.html(html)
                println(count)
            } else {
                val replaceNodes = getReplacementNodes(element)
//                println("REPN ${replaceNodes.size}")
                val pReplacementElements = mutableListOf<Element>()
                for (rNode in replaceNodes) {
                    if (rNode is TextNode && rNode.text().isNotBlank()) {
                        pReplacementElements.add(Element("p").html(rNode.text()))
                    } else if (rNode is Element) {
                        if (rNode.html().isNotBlank())
                            pReplacementElements.add(Element("p").html(rNode.html()))
                        else
                            println("============EMPTY NODE========")
                    } else {
                        println("ERROR - should not get here")
                    }
                }
                element.parent().insertChildren(element.siblingIndex(), pReplacementElements)
                element.remove()
            }
        }
        println()
        println("p 2 ${doc.select("p").size}")

    }

    private fun getReplacementNodes(div: Node): List<Node> {
        val children = div.childNodes()
//        println("======>>${children.size}")
        val nodesToReturn = mutableListOf<Node>()
        val replacmentText = mutableListOf<String>()
        for (kid in children) {
            if (kid is Element && kid.tagName() == "p" && replacmentText.isNotEmpty()) {
//                println("---->HERE - NOT IMPLEMENTED")
            } else if (kid is TextNode) {
//                println("---->HERE TEXT NODE - NOT IMPLEMENTED")
            } else {
                nodesToReturn.add(kid)
            }
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
