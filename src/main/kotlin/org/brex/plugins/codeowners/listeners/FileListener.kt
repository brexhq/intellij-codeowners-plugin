package org.brex.plugins.codeowners.listeners

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import org.brex.plugins.codeowners.CodeOwners
import java.io.File

class FileListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val relPath = File(file.path).relativeTo(File(source.project.basePath!!)).toPath()
        val codeowners = CodeOwners(source.project.basePath!!).getCodeowners(relPath)
        println("relative path: " + File(file.path).relativeTo(File(source.project.basePath!!)))
        println("codeowners: $codeowners")
    }
}
