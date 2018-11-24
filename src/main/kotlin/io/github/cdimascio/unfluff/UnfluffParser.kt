package io.github.cdimascio.unfluff

import org.jsoup.Jsoup

class UnfluffParser(private val html: String, private val language: Language = Language.en) {
    private val document = Jsoup.parse(this.html)
    private val stopWords = StopWords.load(language)
    private val cleaner = Cleaner(document, this.language)
    private val extractor = Extractor(document, this.language, stopWords)
    private val scorer = DocumentScorer(document, language, stopWords)

    fun parse(): UnfluffDocument {

        val title = extractor.title()
        val softTitle = extractor.softTitle()
        val description = extractor.description()
        val authors = extractor.authors()
        val copyright = extractor.copyright()
        val date = extractor.date()
        val favicon = extractor.favicon()
        val publisher = extractor.publisher()
        val image = extractor.image()
        val tags = extractor.tags()
        val canonicalLink = extractor.canonicalLink()
        val language = extractor.lang()
        val keywords = extractor.keywords()

        // clean and score document before extracting text, links and video
        cleaner.clean()
        val node = scorer.score()

        val links = extractor.links(node)
        val videos = extractor.videos(node)
        val text = extractor.text(node)

        return UnfluffDocument(
            authors = authors,
            title = title,
            softTitle = softTitle,
            description = description,
            publisher = publisher,
            date = date,
            copyright = copyright,
            language = language,
            text = text,
            favicon = favicon,
            image = image,
            links = links,
            canonicalLink = canonicalLink,
            keywords = keywords,
            tags = tags
        )
    }


    fun topNode() {

    }

    fun cleanedDoc(): CleanDocument {
        return Cleaner(document, language).clean()
    }
}
