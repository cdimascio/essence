package io.github.cdimascio.essence

object Essence {
    fun extract(html: String): EssenceResult {
        return EssenceParser(html).parse()
    }
    fun extract(html: String, language: Language? = null): EssenceResult {
        return EssenceParser(html, language).parse()
    }
}
