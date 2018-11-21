package io.github.cdimascio.unfluff

enum class Language {
    en
}

interface IDocument {
    val text: String
    val language: Language
}

data class CleanDocument(
    override val text: String,
    override val language: Language
): IDocument

data class FluffDocument(
    override val text: String,
    override val language: Language
): IDocument

data class UnfluffDocument(
    override val text: String,
    override val language: Language,
    val authors: List<String>,
    val title: String
): IDocument