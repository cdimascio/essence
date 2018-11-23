package io.github.cdimascio.unfluff

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class UnfluffSpec {

    @Test
    fun getsCleanedTextPolygon() {
        checkFixture(site = "polygon", fields = listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    private fun cleanTestingTest(text: String): String {
        val textLen = text.length
        return text.
            replace("""\n\n""", " ").
            replace("""\ \ """, " ")
            .substring(textLen, textLen-1)
    }

    private fun cleanOrigText(text: String): String {
        return text.replace("""\n\n""", " ")
    }

    fun checkFixture(site: String, fields: List<String>) {
        val html = readFileFull("./fixtures/test_$site.html")
        val orig = parseJson(readFileFull("./fixtures/test_$site.json"))
        val doc = Jsoup.parse(html)
        val data = Extractor(doc)

        for (field in fields) {
            when (field) {
                "title" -> {
                    assertEquals(orig["expected"]["title"].asText(), data.title())
                }
            }
        }
    }

//    @Test
//    fun cleanDoc() {
//        val html = htmlExample
//        val language = Language.en
//        val cleaned = UnfluffParser(html, language).cleanedDoc()
//        println(cleaned)
//        val pdoc = UnfluffParser(html, language).parsedDoc()
//        println()
//        println()
//        println()
//        println()
//        println()
//
//        println()
//        println("TITLE ${pdoc.title} TEXT ${pdoc.text}")
//        assertEquals("Intel Science Talent Search", pdoc.title)
//
//    }
}
