package com.brex.plugins.codeowners

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager

internal class CodeOwnersProjectManagerListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        //TODO: cleanup, create a VFS listener instead?
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    super.after(events)
                    project.getService(StatusBarWidgetsManager::class.java).updateWidget(CodeOwnersWidgetFactory::class.java)
                }
            }
        )
    }
}
