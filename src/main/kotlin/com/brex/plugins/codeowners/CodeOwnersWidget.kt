package com.brex.plugins.codeowners

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
import java.awt.event.MouseEvent

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    /** Reload CodeOwners if the current file has changed */
    private var codeOwnerFile: VirtualFile? = null
    private var codeOwnerRule: CodeOwnerRule? = null
    private val codeOwnersService: CodeOwners = CodeOwners(project)

    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"
    }

    override fun ID() = ID

    override fun getTooltipText() = "Click to show in CODEOWNERS file"

    override fun getSelectedValue() = "CODEOWNERS: ${makeOwnersDescription(getCurrentCodeOwnerRule())}"

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    /** Return a popup listing all codeowners for a file */
    override fun getPopupStep(): ListPopup? {
        val owners = getCurrentCodeOwnerRule()
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

    /** Open the CODEOWNERS file, and navigate to the line which defines the owner of the current file */
    private fun goToOwner() {
        val codeOwnersFile = codeOwnersService.codeOwnersFile(selectedFile)
        if (codeOwnersFile != null) {
            OpenFileDescriptor(project, codeOwnersFile, codeOwnerRule?.lineNumber ?: 0, 0).navigate(true)
        }
    }

    /** Get CodeOwner rule for the currently opened file */
    private fun getCurrentCodeOwnerRule(): CodeOwnerRule? {
        val file = selectedFile ?: return null
        if (file != codeOwnerFile) {
            codeOwnerFile = selectedFile
            codeOwnerRule = codeOwnersService.getCodeOwners(file)
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
