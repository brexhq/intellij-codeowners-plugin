package org.brex.plugins.codeowners.widget

import com.intellij.ide.lightEdit.LightEditCompatible
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class CodeOwnersWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = CodeOwnersWidget.ID

    override fun getDisplayName() = "CodeOwners Display"

    override fun disposeWidget(widget: StatusBarWidget) {
        Disposer.dispose(widget)
    }

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project) = CodeOwnersWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
