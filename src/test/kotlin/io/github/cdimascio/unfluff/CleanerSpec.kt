package io.github.cdimascio.unfluff


import org.jsoup.Jsoup
import org.jsoup.nodes.Node
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CleanerSpec {

    @Test
    fun removesBodyClasses() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val document = Jsoup.parse(contents)

        assertEquals("magazine", document.body().attr("class").trim())
        val cleaner = Cleaner(document, Language.en)
        val cleaned = cleaner.clean().doc

        println(cleaned.html())
        assertEquals("", cleaned.body().attr("class").trim())
    }

    @Test
    fun removeArticleAttrs() {
        val contents = readFileFull("./fixtures/test_gizmodo1.html")
        val document = Jsoup.parse(contents)

        assertEquals(
            "row post js_post_item status-published commented js_amazon_module",
            document.select("article").attr("class").trim()
        )
        Cleaner(document, Language.en).clean()

        assertEquals(
            "", document.select("article").attr("class").trim()
        )
    }

    @Test
    fun removeEmTagFroImagelessEms() {
        val contents = readFileFull("./fixtures/test_gizmodo1.html")
        val document = Jsoup.parse(contents)

        assertEquals(6, document.select("em").size)

        Cleaner(document, Language.en).clean()
        assertEquals(0, document.select("em").size)
    }

    @Test
    fun removeScripts() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val document = Jsoup.parse(contents)

        assertEquals(40, document.select("script").size)
        Cleaner(document, Language.en).clean()
        assertEquals(0, document.select("script").size)
    }

    @Test
    fun removeComments() {
        val contents = readFileFull("./fixtures/test_gizmodo1.html")
        val document = Jsoup.parse(contents)

        var origComments = 0
        traverse(document) {
            if (it.nodeName() == "#comment") origComments += 1
        }
        assertEquals(18, origComments)

        val doc = Jsoup.parse(contents)
        Cleaner(doc, Language.en).clean()

        var comments = 0
        traverse(doc) {
            if (it.nodeName() == "#comment") comments += 1
        }
        assertEquals(0, comments)
    }

    @Test
    fun replaceChildlessDivsWithPTags() {
        val doc = Jsoup.parse("<html><body><div>text1</div></body></html>")
        Cleaner(doc, Language.en).clean()
        println(doc.html())
        assertEquals(0, doc.select("div").size)
        assertEquals(1, doc.select("p").size)
        assertEquals("text1", doc.select("p").text())
    }

    @Test
    fun replacesUTagsWithPlainText() {
        val doc = Jsoup.parse("<html><body><u>text1</u></body></html>")
        Cleaner(doc, Language.en).clean()
        println(doc.html())
        println("====== ${doc.body().html()}")
        assertEquals(0, doc.select("u").size)
        assertEquals("text1", doc.body().html())
    }
}

fun traverse(node: Node, visit: (Node) -> Unit) {
    visit(node)
    for (c in node.childNodes()) {
        traverse(c, visit)
    }
}
