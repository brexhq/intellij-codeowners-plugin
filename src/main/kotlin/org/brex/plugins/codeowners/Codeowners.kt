package org.brex.plugins.codeowners

import java.nio.file.FileSystems
import java.nio.file.Path

class Codeowners(val basePath: String) {
    fun codeownerRules(): Map<String, List<String>> {
        val rules = mutableMapOf<String, List<String>>()
        // TODO: support different paths (e.g. docs/CODEOWNERS)
        val codeownersPath = Path.of(basePath, "CODEOWNERS")
        if (codeownersPath.toFile().isFile) {
            codeownersPath.toFile().forEachLine {
                if (it.startsWith("#")) {
                    return@forEachLine
                }

                val delim = Regex("\\s+")
                val splits = it.split(delim).toMutableList()

                if (splits.size < 2) {
                    return@forEachLine
                }

                // Special case, if we see a `*` on its own, it should match everything in all dirs
                if (splits[0] == "*") {
                    splits[0] = "**"
                }

                rules[splits[0]] = splits[1].split(delim)
            }
        }
        return rules
    }

    fun getCodeowners(path: Path): List<String> {
        val rules = codeownerRules()
        var codeowners: List<String>? = null
        for ((pattern, owners) in rules) {
            val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
            if (matcher.matches(path)) {
                codeowners = owners
            }
        }

        return codeowners ?: emptyList()
    }
}
