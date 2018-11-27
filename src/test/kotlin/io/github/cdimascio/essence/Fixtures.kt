package io.github.cdimascio.essence

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

private val om = jacksonObjectMapper()
fun parseJson(json: String): JsonNode {
    return om.readTree(json)
}
fun readFileFull(path: String): String {
    val lines = readFileLines(path)
    return lines.joinToString(" ")
}
fun readFileLines(path: String): List<String> {
    val resource = ClassLoader.getSystemResource(path).toURI()
    return Files.lines(Paths.get(resource)).collect(Collectors.toList())
}
