package com.brex.plugins.codeowners

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

data class CodeOwnerRule(
    val pattern: String,
    val owners: List<String>,
    val lineNumber: Int
) {
    companion object {
        fun fromCodeOwnerLine(lineNumber: Int, line: List<String>) =
            CodeOwnerRule(line[0], line.drop(1), lineNumber)
    }
}

class CodeOwners(val project: Project) {
    fun codeownerRules(file: VirtualFile): List<CodeOwnerRule> {
        val codeownersFile = codeownersFile(file) ?: return listOf()

        return codeOwnersFile.toNioPath().toFile()
            .readLines()
            .mapIndexed { index, s -> Pair(index, s) }
            .filter { !it.second.startsWith("#") }
            .map { Pair(it.first, it.second.split("\\s+".toRegex())) }
            .filter { it.second.size >= 2 }
            .map { CodeOwnerRule.fromCodeOwnerLine(it.first, it.second) }
    }

    /** Takes an absolute path and finds a matching CodeOwnerRule, if any */
    fun getCodeOwners(file: VirtualFile): CodeOwnerRule? {
        val rules = codeOwnerRules(file)
        val basePath = getBaseDir(file) ?: return null

        val lastMatch = rules.findLast {
            Glob(basePath, it.pattern, restrictToBaseDir = true, includeChildren = true).matches(file.path)
        }

        return lastMatch
    }

    fun codeOwnersFile(relativeTo: VirtualFile?): VirtualFile? {
        val baseDir = getBaseDir(relativeTo) ?: return null
        // TODO: support different paths (e.g. docs/CODEOWNERS)
        val codeOwnersPath = Path.of(baseDir, "CODEOWNERS")
        return LocalFileSystem.getInstance().findFileByNioFile(codeOwnersPath)
    }

    /** Gets base dir relative to a given project file */
    fun getBaseDir(relativeTo: VirtualFile?): String? {
        val r = relativeTo ?: return null
        return ModuleUtil.findModuleForFile(r, project)?.guessModuleDir()?.toNioPath().toString()
    }
}
