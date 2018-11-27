package io.github.cdimascio.unfluff

object Unfluff {
    fun parse(html: String): UnfluffDocument {
        return UnfluffParser(html).parse()
    }
    fun parse(html: String, language: Language? = null): UnfluffDocument {
        return UnfluffParser(html, language).parse()
    }
}
