package io.github.cdimascio.essence

object Essence {

    @JvmStatic
    fun extract(html: String, language: Language? = null): EssenceResult = EssenceParser(html, language).parse()
}
