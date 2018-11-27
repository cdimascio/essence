package io.github.cdimascio.essence.cleaners.rules

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

private val REGEX_BAD_TAGS = """^side$|combx|retweet|mediaarticlerelated|menucontainer|navbar|partner-gravity-ad|video-full-transcript|storytopbar-bucket|utility-bar|inline-share-tools|comment|PopularQuestions|contact|foot|footer|Footer|footnote|cnn_strycaptiontxt|cnn_html_slideshow|cnn_strylftcntnt|links|meta$|shoutbox|sponsor|tags|socialnetworking|socialNetworking|cnnStryHghLght|cnn_stryspcvbx|^inset$|pagetools|post-attributes|welcome_form|contentTools2|the_answers|communitypromo|runaroundLeft|subscribe|vcard|articleheadings|date|^print$|popup|author-dropdown|tools|socialtools|byline|konafilter|KonaFilter|breadcrumbs|^fn$|wp-caption-text|legende|ajoutVideo|timestamp|js_replies""".toRegex(RegexOption.IGNORE_CASE)
private val REGEX_NAV = """["#.'-_]+nav[-_"']+""".toRegex(RegexOption.IGNORE_CASE)

object Rule {

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

    fun cleanArticleTag(node: Node) {
        if (node is Element && node.tagName() == "article") {
            node.removeAttr("id")
            node.removeAttr("name")
            node.removeAttr("class")
        }
    }
}
