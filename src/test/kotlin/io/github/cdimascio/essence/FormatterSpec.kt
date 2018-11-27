package io.github.cdimascio.essence

import io.github.cdimascio.essence.formatters.TextFormatter
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.Jsoup
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class FormatterSpec {
    private val stopWords = StopWords.load(Language.en)
    private val formatter = TextFormatter(stopWords)
    @Test
    fun replacesLinksWithPlainText() {
        val contents = readFileFull("./fixtures/test_businessWeek1.html")
        val doc = Jsoup.parse(contents)

        val originalLinks = doc.select("a")
        assertEquals(232, originalLinks.size)

        formatter.format(doc)

        val links = doc.select("a")
        assertEquals(0, links.size)
    }

    @Test
    fun doesNotDropTextNodesAccidentally() {
        val contents = readFileFull("./fixtures/test_wikipedia1.html")
        val doc = Jsoup.parse(contents)

        formatter.format(doc)

        assertTrue(doc.html().contains("""
            is a thirteen episode anime series directed by Akitaro Daichi and written by Hideyuki Kurata
            """.trimIndent()))
    }
}
