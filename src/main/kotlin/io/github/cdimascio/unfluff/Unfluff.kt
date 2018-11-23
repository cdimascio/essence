package io.github.cdimascio.unfluff

object Unfluff {
    fun parse(html: String, language: Language = Language.en): UnfluffDocument {
        return UnfluffParser(html, language).parse()
    }
}
