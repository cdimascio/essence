package io.github.cdimascio.essence.words

import io.github.cdimascio.essence.Language
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


data class StopWordsStatistics(
    val wordCount: Int,
    val stopWords: List<String>
)

class StopWords private constructor(private val stopWords: List<String>) {
    companion object {
        fun load(language: Language = Language.en): StopWords {
            val ins = StopWords::class.java.getResourceAsStream("/stopwords/stopwords-$language.txt")
            val words = readFromInputStream(ins)
            return StopWords(words.map {
                it.trim().toLowerCase()
            })
        }

        @Throws(IOException::class)
        private fun readFromInputStream(inputStream: InputStream): List<String> {
            val words = mutableListOf<String>()
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                var line = br.readLine()
                while (line != null) {
                    words += line
                    line = br.readLine()
                }
            }
            return words
        }
    }

    fun statistics(content: String): StopWordsStatistics {
        val cleanedContent = removePunctuation(content)
        val candidates = cleanedContent.split(" ").map { it.toLowerCase() }
        val stopWordsInContent = candidates.filter { word -> stopWords.contains(word) }
        return StopWordsStatistics(
            wordCount = candidates.size,
            stopWords = stopWordsInContent
        )
    }

    private fun removePunctuation(content: String): String =
        content.replace("""[\|\@\<\>\[\]\"\'\.,-\/#\?!$%\^&\*\+;:{}=\-_`~()]""".toRegex(), "")
}
