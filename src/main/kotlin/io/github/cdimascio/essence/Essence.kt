package io.github.cdimascio.essence

object Essence {
    @JvmStatic
    fun extract(html: String): EssenceResult {
        return EssenceParser(html).parse()
    }

    @JvmStatic
    fun extract(html: String, language: Language? = null): EssenceResult {
        return EssenceParser(html, language).parse()
    }
}
