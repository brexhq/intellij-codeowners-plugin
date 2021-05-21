package com.brex.plugins.codeowners

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Path

class CodeOwners(private val project: Project) {
    private val validCodeOwnerPaths = listOf("CODEOWNERS", "docs/CODEOWNERS", ".github/CODEOWNERS", ".gitlab/CODEOWNERS")

    /** Given a file, find a matching CodeOwnerRule in the appropriate CodeOwner file, if any */
    fun getCodeOwners(file: VirtualFile): CodeOwnerRule? {
        val rules = getCodeOwnersRule(file)
        val basePath = getBaseDir(file) ?: return null

        return rules.findLast {
            Glob(basePath, it.pattern, restrictToBaseDir = true, includeChildren = true).matches(file.path)
        }
    }

    /** Given a file, find the CodeOwner file which should govern its ownership, if any */
    fun findCodeOwnersFile(relativeTo: VirtualFile?): File? {
        val baseDir = getBaseDir(relativeTo) ?: return null

        for (validCodeOwnerPath in validCodeOwnerPaths) {
            val f = LocalFileSystem.getInstance().findFileByNioFile(Path.of(baseDir, validCodeOwnerPath))?.toNioPath()?.toFile() ?: continue
            if (f.isFile) return f
        }

        return null
    }

    /** Gets the list of CodeOwnerRules from a CODEOWNERS file relative to the given file */
    private fun getCodeOwnersRule(file: VirtualFile): List<CodeOwnerRule> {
        val codeOwnersFile = findCodeOwnersFile(file) ?: return listOf()

        return codeOwnersFile
            .readLines()
            .asSequence()
            .mapIndexed { index, s -> Pair(index, s) }
            .filter { it.second.isNotEmpty() && !it.second.startsWith("#") }
            .map { Pair(it.first, it.second.split("\\s+".toRegex())) }
            .filter { it.second.size >= 2 }
            .map { CodeOwnerRule.fromCodeOwnerLine(it.first, it.second) }
            .toList()
    }

    /** Find the top-most module directory which contains the given file */
    private fun getBaseDir(relativeTo: VirtualFile?): String? {
        // Sometimes there are weird paths that aren't actually in the filesystem
        // TODO: try to verify the files existance some other way?
        val relPath = try { relativeTo?.toNioPath() } catch (e: Throwable) { return null } ?: return null
        return ModuleManager.getInstance(project).sortedModules
            .mapNotNull { try { it.guessModuleDir()?.toNioPath() } catch (e: Throwable) { return null } }
            .filter { relPath.startsWith(it) }
            .minBy { it.toList().size }
            .toString()
    }
}
