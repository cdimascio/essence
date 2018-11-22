package io.github.cdimascio.unfluff

import junit.framework.Assert.assertEquals
import org.jsoup.Jsoup
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
}
