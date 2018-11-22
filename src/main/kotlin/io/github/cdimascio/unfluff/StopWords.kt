package io.github.cdimascio.unfluff

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class StopWords private constructor(private val stopWords: List<String>) {
    companion object {
        fun load(language: Language = Language.en): StopWords {
            println("./stopwords/stopwords-$language")
            val resource = ClassLoader.getSystemResource("./stopwords/stopwords-$language.txt").toURI()
            val words = Files.lines(Paths.get(resource))
            return StopWords(words.map {
                it.trim().toLowerCase()
            }.collect(Collectors.toList()))
        }
    }

    fun stopwords(): List<String> {
        return stopWords
    }

    fun statistics(content: String): StopWordsStatistics {
        val cleanedContent = removePunctuation(content)
        val candidates = cleanedContent.split(" ").map{ it.toLowerCase().trim() }
        val stopWordsInContent = candidates.filter{ word -> stopWords.contains(word) }
        return StopWordsStatistics(
            wordCount = candidates.size,
            stopWords = stopWordsInContent
        )
    }

    private fun removePunctuation(content: String): String {
        return content.replace("""[\|\@\<\>\[\]\"\'\.,-\/#\?!$%\^&\*\+;:{}=\-_`~()]""", "")
    }

}
