package io.github.cdimascio.unfluff

import org.jsoup.Jsoup
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class FormatterSpec {
    private val stopWords = StopWords.load(Language.en)
    @Test
    fun replacesLinksWithPlainText() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val doc = Jsoup.parse(contents)

        val originalLinks = doc.select("a")
        assertEquals(232, originalLinks.size)

        Formatter(doc, Language.en, stopWords).format(doc)

        val links = doc.select("a")
        assertEquals(0, links.size)
    }

    @Test
    fun doesNotDropTextNodesAccidentally() {
        val contents = readFileFull("./fixtures/test_wikipedia1.html")
        val doc = Jsoup.parse(contents)

        Formatter(doc, Language.en, stopWords).format(doc)

        assertTrue(doc.html().contains("""
            is a thirteen episode anime series directed by Akitaro Daichi and written by Hideyuki Kurata
            """.trimIndent()))
    }
}
