package org.brex.plugins.codeowners.services


import com.intellij.openapi.vfs.VirtualFile
import org.brex.plugins.codeowners.MyBundle

class FileService(file: VirtualFile) {

    init {
        println(MyBundle.message("fileService", file.name))
    }
}
