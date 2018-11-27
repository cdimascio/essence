package io.github.cdimascio.essence.extractors

import io.github.cdimascio.essence.formatters.Formatter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Extractor(private val doc: Document) {
    fun date() = DataExtractor.extract(doc)

    fun copyright() = CopyrightExtractor.extract(doc)

    fun authors() = AuthorExtractor.extract(doc)

    fun publisher() = PublisherExtractor.extract(doc)

    fun title() = TitleExtractor.extract(doc)

    fun softTitle() = SoftTitleExtractor.extract(doc)

    fun favicon() = FaviconExtractor.extract(doc)

    fun description() = DescriptionExtractor.extract(doc)

    fun keywords() = KeywordsExtractor.extract(doc)

    fun lang() = LanguageExtractor.extract(doc)

    fun canonicalLink() = CanonicalExtractor.extract(doc)

    fun tags() = TagsExtractor.extract(doc)

    fun image() = ImageExtractor.extract(doc)

    fun videos(node: Element?) = VideosExtractor.extract(node)

    fun links(node: Element?) = LinksExtractor.extract(node)

    fun text(node: Element?, formatter: Formatter) = TextExtractor.extract(node, formatter)
}



