package org.brex.plugins.codeowners

import java.nio.file.FileSystems
import java.nio.file.Path

data class CodeownerRule(
    val pattern: String,
    val owners: List<String>
) {
    companion object {
        fun fromCodeownerLine(line: List<String>) = CodeownerRule(if (line[0] == "*") "**" else line[0], line.drop(1))
    }
}

class Codeowners(val basePath: String) {
    fun codeownerRules(): List<CodeownerRule> {
        // TODO: support different paths (e.g. docs/CODEOWNERS)
        val codeownersPath = Path.of(basePath, "CODEOWNERS")

        if (!codeownersPath.toFile().isFile) {
            return listOf()
        }

        return codeownersPath.toFile()
            .readLines()
            .filter { !it.startsWith("#") }
            .map { it.split("\\s+".toRegex()) }
            .filter { it.size >= 2 }
            .map { CodeownerRule.fromCodeownerLine(it) }
    }

    fun getCodeowners(path: Path): List<String> {
        val rules = codeownerRules()
        val fs = FileSystems.getDefault()

        val lastMatch = rules.findLast {
            fs.getPathMatcher("glob:${it.pattern}").matches(path)
        }

        return lastMatch?.owners ?: emptyList()
    }
}
