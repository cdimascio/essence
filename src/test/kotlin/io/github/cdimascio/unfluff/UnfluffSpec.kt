package io.github.cdimascio.unfluff

import org.junit.Test
import kotlin.test.assertEquals

class UnfluffSpec {

    @Test
    fun cleanDoc() {
        val html = htmlExample
        val language = Language.en
        val cleaned = UnfluffParser(html, language).cleanedDoc()
        println(cleaned)
        val pdoc = UnfluffParser(html, language).parsedDoc()
        println()
        println()
        println()
        println()
        println()
        println(pdoc.text)
        println()
        assertEquals("Intel Science Talent Search", pdoc.title)
    }
}
