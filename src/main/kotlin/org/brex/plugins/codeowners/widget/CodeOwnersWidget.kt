package org.brex.plugins.codeowners.widget

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    override fun ID(): String {
        return ID
    }

    override fun getTooltipText(): String? = "Tooltop!"
    override fun getSelectedValue(): String? = "CodeOwner: ${selectedFile?.name}"

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getPopupStep(): ListPopup? {
        return null
    }

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = this

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        myStatusBar.updateWidget(ID())
    }

    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"
    }
}
