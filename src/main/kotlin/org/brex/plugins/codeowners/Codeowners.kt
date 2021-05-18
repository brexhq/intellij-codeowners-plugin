package org.brex.plugins.codeowners

import java.nio.file.FileSystems
import java.nio.file.Path

data class CodeownerRule(
    val pattern: String,
    val owners: List<String>,
    val lineNumber: Int
) {
    companion object {
        fun fromCodeownerLine(lineNumber: Int, line: List<String>) =
            CodeownerRule(if (line[0] == "*") "**" else line[0], line.drop(1), lineNumber)
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
            .mapIndexed { index, s -> Pair(index, s) }
            .filter { !it.second.startsWith("#") }
            .map { Pair(it.first, it.second.split("\\s+".toRegex())) }
            .filter { it.second.size >= 2 }
            .map { CodeownerRule.fromCodeownerLine(it.first + 1, it.second) }
    }

    fun getCodeowners(path: Path): CodeownerRule? {
        val rules = codeownerRules()
        val fs = FileSystems.getDefault()

        val lastMatch = rules.findLast {
            fs.getPathMatcher("glob:${it.pattern}").matches(path)
        }

        return lastMatch
    }
}
