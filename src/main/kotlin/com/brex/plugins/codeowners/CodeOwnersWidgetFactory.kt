package com.brex.plugins.codeowners

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class CodeOwnersWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = CodeOwnersWidget.ID

    override fun getDisplayName() = "Code Owners"

    override fun disposeWidget(widget: StatusBarWidget) = Disposer.dispose(widget)

    // TODO: We can use this to only enable the widget if any project module contains a CODEOWNER, but then we have to handle all cases where CODEOWNERS gets added
    // override fun isAvailable(project: Project) = ModuleManager.getInstance(project).modules.any() { module -> CodeOwners(project).findCodeOwnersFile(module.moduleFile) !== null }

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project) = CodeOwnersWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
