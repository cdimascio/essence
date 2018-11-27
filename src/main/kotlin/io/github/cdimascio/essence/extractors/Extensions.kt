package io.github.cdimascio.essence.extractors

fun String.cleanse(): String {
    return this.trim()
        .replace("null", "")
        .replace("""[\r\n\t]""".toRegex(), " ")
        .replace("""\s\s+""".toRegex(), " ")
        .replace("""<!--.+?-->""", "")
        .replace("""�""", "")
}
