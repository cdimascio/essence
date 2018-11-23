package io.github.cdimascio.unfluff

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNotEquals

class UnfluffSpec {

    @Test
    fun getsCleanedTextPolygon() {
        checkFixture(site = "polygon", fields = listOf("cleaned_text", "title", "link", "description", "lang", "favicon"))
    }

    private fun cleanTestingTest(newText: String, originalText: String): String {
        return newText.
            replace("""\n\n""", " ").
            replace("""\ \ """, " ")
            .substring(0, Math.min(newText.length, originalText.length))
    }

    private fun cleanOrigText(text: String): String {
        return text.replace("""\n\n""", " ")
    }

    fun checkFixture(site: String, fields: List<String>) {
        val html = readFileFull("./fixtures/test_$site.html")
        val orig = parseJson(readFileFull("./fixtures/test_$site.json"))
//        val origDoc = Jsoup.parse(html)
        val data = Unfluff.parse(html, Language.en)

        for (field in fields) {
            when (field) {
                "title" -> {
                    assertEquals(orig["expected"]["title"].asText(), data.title ?: "")
                }
                "cleaned_text" -> {
                    val origText = orig["expected"]["cleaned_text"].asText()
                    val newText = cleanTestingTest(data.text ?: "", origText)
                    assertNotEquals("", newText)
                    assertTrue(data.text?.length ?: 0 >= origText.length)
                    assertEquals(origText, newText)

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
