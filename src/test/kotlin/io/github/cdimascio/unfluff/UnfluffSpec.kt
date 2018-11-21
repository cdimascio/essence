package io.github.cdimascio.unfluff

import org.junit.Test
import kotlin.test.assertEquals

class UnfluffSpec {

    @Test
    fun cleanDoc() {
        val html = htmlExample
        val language = Language.en
//        val pdoc = UnfluffParser(html, language).cleanedDoc()
        val pdoc = UnfluffParser(html, language).parsedDoc()
        println(pdoc)
        assertEquals("Intel Science Talent Search", pdoc.title)
    }
}
