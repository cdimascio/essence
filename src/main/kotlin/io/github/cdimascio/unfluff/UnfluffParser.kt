package io.github.cdimascio.unfluff

import org.jsoup.Jsoup

class UnfluffParser(private val html: String, language: Language? = null) {
    private val document = Jsoup.parse(this.html)
    private val cleaner = Cleaner(document)
    private val extractor = Extractor(document)
    private val language = Language.from(extractor.lang())
    private val stopWords = StopWords.load(this.language)
    private val scorer = DocumentScorer(document, stopWords)
    private val formatter = Formatter(stopWords)

    fun parse(): UnfluffDocument {
        println("---start 1 ${document.select("div").size}")
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
        val keywords = extractor.keywords()

        println("---start 2 ${document.select("div").size}")
        // clean and score document before extracting text, links and video
        cleaner.clean()
        println("---start 3 ${document.select("div").size}")
        val topNode = scorer.score()

        val links = extractor.links(topNode)
        val videos = extractor.videos(topNode)
        val text = extractor.text(topNode, formatter)

        return UnfluffDocument(
            authors = authors,
            title = title,
            softTitle = softTitle,
            description = description,
            publisher = publisher,
            date = date,
            copyright = copyright,
            language = language.name,
            text = text,
            favicon = favicon,
            image = image,
            links = links,
            canonicalLink = canonicalLink,
            keywords = keywords,
            tags = tags
        )
    }
}
