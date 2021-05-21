package com.brex.plugins.codeowners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.nio.file.Path

class CodeOwners(private val project: Project) {
    private val validCodeOwnerPaths = listOf(
        "CODEOWNERS",
        "docs/CODEOWNERS",
        ".github/CODEOWNERS",
        ".gitlab/CODEOWNERS"
    )
    private val logger = Logger.getInstance(this.javaClass)

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
        val fs = LocalFileSystem.getInstance()

        return validCodeOwnerPaths.asSequence()
            .mapNotNull { path -> fs.findFileByNioFile(Path.of(baseDir, path))?.tryGetNioPath()?.toFile() }
            .firstOrNull { it.isFile }
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
        val relPath = relativeTo?.tryGetNioPath() ?: return null
        return ModuleManager.getInstance(project).sortedModules
            .mapNotNull { it.guessModuleDir()?.tryGetNioPath() }
            .filter { relPath.startsWith(it) }
            .minBy { it.toList().size }
            .toString()
    }

    /** Try to get the path of a VirtualFile, or return null if it fails */
    private fun VirtualFile.tryGetNioPath(): Path? {
        // Sometimes there are weird paths that aren't actually in the filesystem
        // TODO: try to verify the files existence some other way?
        return try {
            this.toNioPath()
        } catch (e: UnsupportedOperationException) {
            logger.error(e)
            null
        }
    }
}
