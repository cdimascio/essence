package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document

enum class Language {
    en
}

interface IDocument {
    val text: String?
    val language: Language
}

data class CleanDocument(
    override val text: String?,
    override val language: Language,
    val doc: Document
): IDocument

data class FluffDocument(
    override val text: String?,
    override val language: Language
): IDocument

data class UnfluffDocument(
    override val text: String?,
    override val language: Language,
    val authors: List<String>,
    val title: String?,
    val copyright: String?,
    val date: String?,
    val publisher: String?

): IDocument

data class StopWordsStatistics(
    val wordCount: Int,
    val stopWords: List<String>
)
