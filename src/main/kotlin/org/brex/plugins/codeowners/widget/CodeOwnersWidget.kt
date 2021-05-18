package org.brex.plugins.codeowners.widget

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import org.brex.plugins.codeowners.CodeOwnerRule
import org.brex.plugins.codeowners.CodeOwners
import java.awt.event.MouseEvent
import java.io.File

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"
    }

    override fun ID(): String = ID

    override fun getTooltipText(): String? = "Click to show in CODEOWNERS file"

    override fun getSelectedValue(): String? = "CODEOWNERS: ${makeOwnersDescription(codeOwners)}"

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = this

    /** Return a popup listing all codeowners for a file */
    override fun getPopupStep(): ListPopup {
        return JBPopupFactory.getInstance().createListPopup(object : BaseListPopupStep<String>("All CODEOWNERS", codeOwners?.owners) {
            override fun onChosen(selectedValue: String?, finalChoice: Boolean): PopupStep<*>? {
                val codeownersFile = CodeOwners(project.basePath!!).codeownersFile()
                if (codeownersFile != null) {
                    OpenFileDescriptor(project, codeownersFile, codeOwnerRule?.lineNumber ?: 0, 0).navigate(true)
                }

                return super.onChosen(selectedValue, finalChoice)
            }
        })
    }

    /** Reload CodeOwners if the current file has changed */
    private var codeOwnerFile: VirtualFile? = null
    private var codeOwnerRule: CodeOwnerRule? = null
    private val codeOwnersService: CodeOwners? = project.basePath?.let { CodeOwners(it) }

    private val codeOwners: CodeOwnerRule?
        get() {
            val file = selectedFile ?: return null
            if (selectedFile !== codeOwnerFile) {
                codeOwnerFile = selectedFile
                codeOwnerRule = codeOwnersFromFile(file)
            }
            return codeOwnerRule
        }

    /** Update status bar text when opening a new file */
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        myStatusBar.updateWidget(ID())
    }

    /** Update status bar text when changing switching file */
    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        myStatusBar.updateWidget(ID())
    }

    /** Load codeowners from a file */
    private fun codeOwnersFromFile(file: VirtualFile): CodeOwnerRule? {
        val relPath = File(file.path).relativeTo(File(project.basePath!!)).toPath()
        return codeOwnersService?.getCodeowners(relPath)
    }
}

/** Describe a list of codeowners */
fun makeOwnersDescription(codeOwners: CodeOwnerRule?): String {
    val owners = codeOwners?.owners ?: return "None"
    return owners.first() + when {
        (owners.size == 2) -> " & 1 other"
        (owners.size > 2) -> " & ${owners.size - 1} others"
        else -> ""
    }
}
