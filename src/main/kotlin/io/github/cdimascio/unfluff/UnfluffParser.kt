package io.github.cdimascio.unfluff

import org.jsoup.Jsoup
import javax.swing.text.Document

class UnfluffParser(private val html: String, private val language: Language = Language.en) {
    private val extractor = Extractor(this.html, this.language)
    private val document = Jsoup.parse(this.html)


    fun parsedDoc(): UnfluffDocument {
        return UnfluffDocument(
            text = document.text(),
            title = "",
            language = language,
            authors = emptyList()
        )
    }

    fun topNode() {
//        Extractor
    }

    fun cleanedDoc(): CleanDocument {
        return Cleaner(document, language).clean()
    }
}