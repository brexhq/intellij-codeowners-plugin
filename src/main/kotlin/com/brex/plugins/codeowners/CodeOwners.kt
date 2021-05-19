package com.brex.plugins.codeowners

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

class CodeOwners(private val project: Project) {
    /** Given a file, find a matching CodeOwnerRule in the appropriate CodeOwner file, if any */
    fun getCodeOwnersRule(file: VirtualFile): CodeOwnerRule? {
        val rules = codeOwnerRules(file)
        val basePath = getBaseDir(file) ?: return null

        return rules.findLast {
            Glob(basePath, it.pattern, restrictToBaseDir = true, includeChildren = true).matches(file.path)
        }
    }

    /** Parse Code Owner rules from a CODEOWNERS file */
    private fun codeOwnerRules(file: VirtualFile): List<CodeOwnerRule> {
        val codeOwnersFile = findCodeOwnersFile(file) ?: return listOf()

        return codeOwnersFile.toNioPath().toFile()
            .readLines()
            .mapIndexed { index, s -> Pair(index, s) }
            .filter { !it.second.startsWith("#") }
            .map { Pair(it.first, it.second.split("\\s+".toRegex())) }
            .filter { it.second.size >= 2 }
            .map { CodeOwnerRule.fromCodeOwnerLine(it.first, it.second) }
    }

    /** Given a file, find the CodeOwner file which should govern its ownership, if any */
    fun findCodeOwnersFile(relativeTo: VirtualFile?): VirtualFile? {
        val baseDir = getBaseDir(relativeTo) ?: return null
        // TODO: support different paths (e.g. docs/CODEOWNERS)
        val codeOwnersPath = Path.of(baseDir, "CODEOWNERS")
        return LocalFileSystem.getInstance().findFileByNioFile(codeOwnersPath)
    }

    /** Gets base dir relative to a given project file */
    private fun getBaseDir(relativeTo: VirtualFile?): String? {
        val r = relativeTo ?: return null
        return ModuleUtil.findModuleForFile(r, project)?.guessModuleDir()?.toNioPath().toString()
    }
}
