package org.brex.plugins.codeowners.widget

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import org.brex.plugins.codeowners.Codeowners
import java.awt.event.MouseEvent
import java.io.File

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    override fun ID(): String {
        return ID
    }

    override fun getTooltipText(): String? = "Tooltop!"
    override fun getSelectedValue(): String? {
        val file = selectedFile ?: return null
        val relPath = File(file?.path).relativeTo(File(project.basePath!!)).toPath()
        val codeowners = Codeowners(project.basePath!!).getCodeowners(relPath)
        return "CodeOwner: ${codeowners.joinToString(",")}"
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getPopupStep(): ListPopup? {

        return JBPopupFactory.getInstance().createListPopup(object: BaseListPopupStep<String>("Foo", "Bar", "Baz") {
            override fun onChosen(selectedValue: String?, finalChoice: Boolean): PopupStep<*>? {
                println(selectedValue)
                return super.onChosen(selectedValue, finalChoice)
            }
        })
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = this

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        println("File opened! ${file.path}")
        myStatusBar.updateWidget(ID())
    }

    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        println(event)
        myStatusBar.updateWidget(ID())
    }

    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"
    }
}
