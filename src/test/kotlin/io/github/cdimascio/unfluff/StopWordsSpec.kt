package io.github.cdimascio.unfluff

import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class StopWordsSpec {

    @Test
    fun countStopwords() {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this is silly")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    fun stripsPunctuation() {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this! is?? silly....")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    fun defaultsToEnglish() {
        val stopwords = StopWords.load(Language.en)
        val stats = stopwords.statistics("this is fun")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("this", "is"))
    }

    @Test
    @Ignore
    fun handlesSpanish() {
        val stopwords = StopWords.load(Language.es)
        val stats = stopwords.statistics("este es rico")
        assertEquals(3, stats.wordCount)
        assertEquals(2, stats.stopWords.size)
        stats.stopWords.containsAll(listOf("este", "es"))
    }

}
