package org.brex.plugins.codeowners.widget

import com.intellij.ide.lightEdit.LightEditCompatible
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class CodeOwnersWidgetFactory : StatusBarWidgetFactory, LightEditCompatible {
    init {
        println("Factory init")
    }
    override fun getId() = "org.brex.plugins.codeowners"

    override fun getDisplayName() = "CodeOwners Display"

    override fun disposeWidget(widget: StatusBarWidget) {
//        Disposer.dispose(widget)
    }

    override fun isAvailable(project: Project): Boolean {
        println("isAvailable")
        return true
    }

    override fun createWidget(project: Project) = CodeOwnersWidget()

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        println("canBeEnabledOn")
        return true
    }

    override fun isEnabledByDefault(): Boolean {
        println("isEnabledByDefault")
        return true
    }

    override fun isConfigurable(): Boolean {
        println("isConfigurable")
        return true
    }
}
