package io.github.cdimascio.unfluff

import org.jsoup.Jsoup
import javax.swing.text.Document

class UnfluffParser(private val html: String, private val language: Language = Language.en) {
    private val document = Jsoup.parse(this.html)
    private val extractor: Extractor
    private val cleaner = Cleaner(document, this.language)
    init {
        val cleanedDoc = Jsoup.parse(cleaner.clean().text)
        extractor = Extractor(cleanedDoc, this.language)


    }



    fun parsedDoc(): UnfluffDocument {
        return UnfluffDocument(
            text = extractor.text(),
            title = extractor.title(),
            language = language,
            authors = extractor.authors(),
            copyright = extractor.copyright(),
            publisher = extractor.publisher(),
            date = extractor.date()
        )
    }

    fun topNode() {
//        Extractor
    }

    fun cleanedDoc(): CleanDocument {
        return Cleaner(document, language).clean()
    }
}
