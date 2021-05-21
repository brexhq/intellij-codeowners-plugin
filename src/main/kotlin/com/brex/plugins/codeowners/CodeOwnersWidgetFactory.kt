package com.brex.plugins.codeowners

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class CodeOwnersWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = CodeOwnersWidget.ID

    override fun getDisplayName() = "Code Owners"

    override fun disposeWidget(widget: StatusBarWidget) = Disposer.dispose(widget)

    override fun isAvailable(project: Project): Boolean {
        return ModuleManager.getInstance(project).modules.any { module ->
            CodeOwners(project).findCodeOwnersFile(module.guessModuleDir()) !== null
        }
    }

    override fun createWidget(project: Project) = CodeOwnersWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
