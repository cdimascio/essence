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
        val cleaner = Cleaner(document)
        val cleaned = cleaner.clean().doc

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
        Cleaner(document).clean()

        assertEquals(
            "", document.select("article").attr("class").trim()
        )
    }

    @Test
    fun removeEmTagFroImagelessEms() {
        val contents = readFileFull("./fixtures/test_gizmodo1.html")
        val document = Jsoup.parse(contents)

        assertEquals(6, document.select("em").size)

        Cleaner(document).clean()
        assertEquals(0, document.select("em").size)
    }

    @Test
    fun removeScripts() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val document = Jsoup.parse(contents)

        assertEquals(40, document.select("script").size)
        Cleaner(document).clean()
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
        Cleaner(doc).clean()

        var comments = 0
        traverse(doc) {
            if (it.nodeName() == "#comment") comments += 1
        }
        assertEquals(0, comments)
    }

    @Test
    fun replaceChildlessDivsWithPTags() {
        val doc = Jsoup.parse("<html><body><div>text1</div></body></html>")
        Cleaner(doc).clean()
        assertEquals(0, doc.select("div").size)
        assertEquals(1, doc.select("p").size)
        assertEquals("text1", doc.select("p").text())
    }

    @Test
    fun replaceChildlessDivsWithPTags2() {
        val doc = Jsoup.parse("<html><body><div>text1</div><div>more text</div></body></html>")
        Cleaner(doc).clean()
        assertEquals(0, doc.select("div").size)
        assertEquals(2, doc.select("p").size)
        assertEquals("text1 more text", doc.select("p").text())
    }

    @Test
    fun replacesUTagsWithPlainText() {
        val doc = Jsoup.parse("<html><body><u>text1</u></body></html>")
        Cleaner(doc).clean()
        assertEquals(0, doc.select("u").size)
        assertEquals("text1", doc.body().html())
    }

    @Test
    fun removesDivsByRegExCaption() {
        val contents = readFileFull("./fixtures/test_aolNews.html")
        val doc = Jsoup.parse(contents)

        assertEquals(1, doc.select("div.caption").size)

        Cleaner(doc).clean()

        assertEquals(0, doc.select("div.caption").size)
    }

    @Test
    fun removeNaughtElmsByRegex() {
        val contents = readFileFull("./fixtures/test_issue28.html")
        val doc = Jsoup.parse(contents)

        val naughtyElmsOrig = doc.select(".retweet")
        assertEquals(2, naughtyElmsOrig.size)

        Cleaner(doc).clean()

        val naughtyElms = doc.select(".retweet")
        assertEquals(0, naughtyElms.size)
    }

    @Test
    fun removeTrashLineBreaksThatWouldntBeRenderedByTheBrowser() {
        val contents = readFileFull("./fixtures/test_sec1.html")
        val doc = Jsoup.parse(contents)

        Cleaner(doc).clean()

        val pElements = doc.select("p")
        val cleanedParaText = pElements[9].textNodes()[0].text()
        assertEquals("“This transaction would not only strengthen our global presence, but also demonstrate our commitment to diversify and expand our U.S. commercial portfolio with meaningful new therapies,” said Russell Cox, executive vice president and chief operating officer of Jazz Pharmaceuticals plc. “We look forward to ongoing discussions with the FDA as we continue our efforts toward submission of an NDA for defibrotide in the U.S. Patients in the U.S. with severe VOD have a critical unmet medical need, and we believe that defibrotide has the potential to become an important treatment option for these patients.”", cleanedParaText.trim())
    }

    @Test
    fun inlinesCodeBlocksAsText() {
        val contents = readFileFull("./fixtures/test_github1.html")
        val doc = Jsoup.parse(contents)

        val codeBlocksOrig = doc.select("code")
        assertEquals(26, codeBlocksOrig.size)

        Cleaner(doc).clean()

        val codeBlocks = doc.select("code")
        assertEquals(0, codeBlocks.size)

        // This is a code block that should still be present in the doc after cleaning
        assertTrue(doc.body().text().indexOf("extractor = require('unfluff');") > 0)
    }
}

fun traverse(node: Node, visit: (Node) -> Unit) {
    visit(node)
    for (c in node.childNodes()) {
        traverse(c, visit)
    }
}
