package org.brex.plugins.codeowners

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.FileSystems
import java.nio.file.Path

data class CodeOwnerRule(
    val pattern: String,
    val owners: List<String>,
    val lineNumber: Int
) {
    companion object {
        fun fromCodeownerLine(lineNumber: Int, line: List<String>) =
            CodeOwnerRule(if (line[0] == "*") "**" else line[0], line.drop(1), lineNumber)
    }
}

class CodeOwners(val basePath: String) {
    fun codeownerRules(): List<CodeOwnerRule> {
        val codeownersFile = codeownersFile() ?: return listOf()

        return codeownersFile.toNioPath().toFile()
            .readLines()
            .mapIndexed { index, s -> Pair(index, s) }
            .filter { !it.second.startsWith("#") }
            .map { Pair(it.first, it.second.split("\\s+".toRegex())) }
            .filter { it.second.size >= 2 }
            .map { CodeOwnerRule.fromCodeownerLine(it.first, it.second) }
    }

    fun getCodeowners(path: Path): CodeOwnerRule? {
        val rules = codeownerRules()
        val fs = FileSystems.getDefault()

        val lastMatch = rules.findLast {
            fs.getPathMatcher("glob:${it.pattern}").matches(path)
        }

        return lastMatch
    }

    fun codeownersFile(): VirtualFile? {
        // TODO: support different paths (e.g. docs/CODEOWNERS)
        val codeownersPath = Path.of(basePath, "CODEOWNERS")
        return LocalFileSystem.getInstance().findFileByNioFile(codeownersPath)
    }
}
