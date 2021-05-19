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
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.refactoring.listeners.RefactoringEventData
import com.intellij.refactoring.listeners.RefactoringEventListener
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation, RefactoringEventListener {
    private var currentFilePath: String? = null
    private var currentFileRule: CodeOwnerRule? = null
    private val codeOwnersService: CodeOwners = CodeOwners(project)

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
        myConnection.subscribe(RefactoringEventListener.REFACTORING_EVENT_TOPIC, this)
    }

    override fun ID() = ID

    override fun getTooltipText() = "Click to show in CODEOWNERS file"

    override fun getSelectedValue() = "Owner: ${makeOwnersDescription(getCurrentCodeOwnerRule())}"

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    /** Return a popup listing all code owners for a file */
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
        val codeOwnersFile = codeOwnersService.findCodeOwnersFile(selectedFile)
        if (codeOwnersFile != null) {
            OpenFileDescriptor(project, codeOwnersFile, currentFileRule?.lineNumber ?: 0, 0).navigate(true)
        }
    }

    /** Get CodeOwner rule for the currently opened file */
    private fun getCurrentCodeOwnerRule(): CodeOwnerRule? {
        // Reload CodeOwners if the current file has changed
        val file = selectedFile ?: return null
        if (file.path != currentFilePath) {
            currentFilePath = file.path
            currentFileRule = codeOwnersService.getCodeOwnersRule(file)
        }
        return currentFileRule
    }

    // Listen to Editor events and update the status bar when switching or renaming files
    private fun update() = myStatusBar.updateWidget(ID())
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) = update()
    override fun selectionChanged(event: FileEditorManagerEvent) = update()
    override fun undoRefactoring(refactoringId: String) = update()
    override fun refactoringDone(refactoringId: String, afterData: RefactoringEventData?) = update()
    override fun refactoringStarted(refactoringId: String, beforeData: RefactoringEventData?) {}
    override fun conflictsDetected(refactoringId: String, conflictsData: RefactoringEventData) {}

    companion object {
        internal const val ID = "org.brex.plugins.codeowners.CodeOwnersWidget"

        /** Describe a list of code owners */
        private fun makeOwnersDescription(codeOwners: CodeOwnerRule?): String {
            val owners = codeOwners?.owners ?: return "None"
            return owners.first() + when {
                (owners.size == 2) -> " & 1 other"
                (owners.size > 2) -> " & ${owners.size - 1} others"
                else -> ""
            }
        }
    }
}
