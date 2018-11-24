package io.github.cdimascio.unfluff

import org.jsoup.nodes.Document

enum class Language {
    en,
    es
}

data class CleanDocument(
    val text: String?,
    val doc: Document
)

data class UnfluffDocument(
    val text: String?,
    val language: String?,
    val authors: List<String>,
    val title: String?,
    val softTitle: String?,
    val copyright: String?,
    val date: String?,
    val publisher: String?,
    val description: String?,
    val favicon: String?,
    val image: String?,
    val links: List<Link>,
    val canonicalLink: String,
    val keywords: String,
    val tags: List<String>

)

data class StopWordsStatistics(
    val wordCount: Int,
    val stopWords: List<String>
)

data class Link(
    val href: String,
    val text: String
)
