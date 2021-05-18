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

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"
    }

    override fun ID(): String = ID

    override fun getTooltipText(): String? = "Click to show in CODEOWNERS file"

    override fun getSelectedValue(): String? = "CODEOWNERS: ${makeOwnersDescription(getCodeOwners())}"

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = this

    /** Return a popup listing all codeowners for a file */
    override fun getPopupStep(): ListPopup? {
        val owners = getCodeOwners()
        if (owners === null || owners.owners.size <= 1) {
            // Not sure if there's a better place to handle clicking when there's
            // only one owner.
            goToOwner()
            return null
        }

        return JBPopupFactory.getInstance().createListPopup(object : BaseListPopupStep<String>("All CODEOWNERS", owners.owners) {
            override fun onChosen(selectedValue: String?, finalChoice: Boolean): PopupStep<*>? {
                goToOwner()
                return super.onChosen(selectedValue, finalChoice)
            }
        })
    }

    private fun goToOwner() {
        val codeOwnersFile = codeOwnersService?.codeownersFile()
        if (codeOwnersFile != null) {
            OpenFileDescriptor(project, codeOwnersFile, codeOwnerRule?.lineNumber ?: 0, 0).navigate(true)
        }
    }

    /** Reload CodeOwners if the current file has changed */
    private var codeOwnerFile: VirtualFile? = null
    private var codeOwnerRule: CodeOwnerRule? = null
    private val codeOwnersService: CodeOwners? = project.basePath?.let { CodeOwners(it) }

    private fun getCodeOwners(): CodeOwnerRule? {
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

    /** Load code owners from a file */
    private fun codeOwnersFromFile(file: VirtualFile): CodeOwnerRule? {
        return codeOwnersService?.getCodeowners(file.path)
    }
}

/** Describe a list of code owners */
fun makeOwnersDescription(codeOwners: CodeOwnerRule?): String {
    val owners = codeOwners?.owners ?: return "None"
    return owners.first() + when {
        (owners.size == 2) -> " & 1 other"
        (owners.size > 2) -> " & ${owners.size - 1} others"
        else -> ""
    }
}
