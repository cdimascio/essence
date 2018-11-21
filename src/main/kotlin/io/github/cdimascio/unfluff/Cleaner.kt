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
            .applyRules(doc)
            .purgeMarkedNodes()
        cleanParaSpans()
        cleanUnderlines()

        return CleanDocument(
            text = d.html(),
            language = language
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
        }
    }

    private fun cleanEmTags() {
        val ems = d.getElementsByTag("em")
        ems.forEach {
            val images = ems.select("img")
            if (images.isEmpty()) {
                it.replaceWith(Element(it.html()))
            }
        }
    }

    private fun cleanCodeBlocks() {
        val nodes = d.select(
            "[class*='highlight-'], pre code, code, pre, ul.task-list"
        )
        nodes.forEach {
            it.replaceWith(TextNode(it.text()))
        }
    }

    private fun removeDropCaps() {
        val nodes = d.select("span[class~=dropcap], span[class~=drop_cap]")
        return nodes.forEach {
            return it.replaceWith(Element(it.html()))
        }
    }

    private fun removeScriptsStyles() {
        d.getElementsByTag("script").remove()
        d.getElementsByTag("style").remove()
    }

    private fun cleanParaSpans() {
        d.select("p span").forEach {
            val html = it.html()
            if (html.isNullOrBlank()) {
                it.replaceWith(TextNode(""))
            } else {
                it.replaceWith(Element(html))
            }
        }
    }

    private fun cleanUnderlines() {
        d.select("u").forEach {
            it.replaceWith(Element(it.html()))
        }
    }

//    private fun traverse(node: Node) {
//        for (child in node.childNodes()) {
//            if (removeCommentsTravRule(node)) continue
//            if (removeBadTagsTravRule(node)) continue
//        }
//    }
//
//    /**
//     * Remove node if a comment nodes and return true, else return false
//     */
//    private fun removeCommentsTravRule(node: Node): Boolean {
//        if (node.nodeName() == "#comment") {
//            node.remove()
//            return true
//        }
//        return false
//    }
//
//    private fun removeBadTagsTravRule(node: Node): Boolean {
//        if (node.attr("id").matches(REGEX_BAD_TAGS) ||
//            node.attr("class").matches(REGEX_BAD_TAGS) ||
//            node.attr("name").matches(REGEX_BAD_TAGS)) {
//            node.remove()
//            return true
//        }
//        return false
//    }
}

class Traverse(val nodeRemovalRules: List<(Node) -> Boolean>, val nodeModificationRules: List<(Node) -> Unit>) {
    private var nodesToRemove = setOf<Node>()
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
        nodesToRemove.forEach{
            it.remove()
        }
    }

}

object TraversalRules {
    /**
     * Remove node if a comment nodes and return true, else return false
     */
    fun removeCommentsTravRule(node: Node): Boolean {
        if (node.nodeName() == "#comment") {
//            node.remove()
            return true
        }
        return false
    }

    fun removeBadTagsTravRule(node: Node): Boolean {
        if (node.attr("id").matches(REGEX_BAD_TAGS) ||
            node.attr("class").matches(REGEX_BAD_TAGS) ||
            node.attr("name").matches(REGEX_BAD_TAGS)) {
//            node.remove()
            return true
        }
        return false
    }

    fun removeMatching(re: Regex): (Node) -> Boolean {
        return { node: Node ->
            node.attr("id").matches(re) ||
                node.attr("class").matches(re)
        }
    }

    fun correctErrantLineBreaks(node: Node) {
        if (node is Element && node.tag().name == "p") {
            for (textNode in node.textNodes()) {
                val text = textNode.text()
                text.replace("""([^\n])\n([^\n])""".toRegex()) {
                    it.groupValues.joinToString(" ")
                }
                textNode.text(text)
            }
        }
    }
}

//    private fun removeComments() {
//        fun removeCommentsRecurse(node: Node) {
//            for (child in node.childNodes()) {
//                if (child.nodeName() == "#comment") child.remove()
//                else removeCommentsRecurse(child)
//            }
//        }
//        removeCommentsRecurse(d)
//    }