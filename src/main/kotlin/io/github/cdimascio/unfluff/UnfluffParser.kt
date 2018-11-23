package io.github.cdimascio.unfluff

import org.jsoup.Jsoup

class UnfluffParser(private val html: String, private val language: Language = Language.en) {
    private val document = Jsoup.parse(this.html)

    private val cleaner = Cleaner(document, this.language)
    init {

    }

    fun parse(): UnfluffDocument {
        val e = Extractor(document, this.language)
        val title = e.title()
        val softTitle = e.softTitle()
        val desription = e.description()
        val authors = e.authors()
        val copyright = e.copyright()
        val date = e.date()
        val favicon = e.favicon()
        val publisher = e.publisher()
        val image = e.image()
        val tags = e.tags()
        val canonicalLink = e.canonicalLink()
        val lang = e.lang()
        val keywords = e.keywords()

        Cleaner(document, this.language).clean()

        val links = e.links()
        val videos = e.videos()
        val text = e.text()

        return UnfluffDocument(
            authors = authors,
            title = title,
            softTitle = softTitle,
            description = desription,
            publisher = publisher,
            date = date,
            copyright = copyright,
            language = language,
            text = text,
            favicon = favicon,
            image = image


        )
    }


    fun topNode() {

    }

    fun cleanedDoc(): CleanDocument {
        return Cleaner(document, language).clean()
    }
}
