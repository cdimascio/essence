package io.github.cdimascio.essence

import io.github.cdimascio.essence.cleaners.Cleaner
import io.github.cdimascio.essence.cleaners.ScoreCleaner
import io.github.cdimascio.essence.extractors.Extractor
import io.github.cdimascio.essence.formatters.TextFormatter
import io.github.cdimascio.essence.scorers.DocumentScorer
import io.github.cdimascio.essence.words.StopWords
import org.jsoup.Jsoup

class EssenceParser(private val html: String, language: Language? = null) {
    private val document = Jsoup.parse(this.html)
    private val cleaner = Cleaner(document)
    private val extractor = Extractor(document)
    private val language = language ?: Language.from(extractor.lang())
    private val stopWords = StopWords.load(this.language)
    private val scorer = DocumentScorer(stopWords)
    private val scoredCleaner = ScoreCleaner(stopWords)
    private val formatter = TextFormatter(stopWords)

    fun parse(): EssenceResult {
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

        // clean and score document before extracting text, links and video
        val doc = cleaner.clean()
        val node = scorer.score(doc)
        val topNode = scoredCleaner.clean(node)

        val links = extractor.links(topNode)
        val videos = extractor.videos(topNode)
        val text = extractor.text(topNode, formatter)

        return EssenceResult(
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
