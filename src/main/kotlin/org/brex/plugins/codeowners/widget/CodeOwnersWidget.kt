package org.brex.plugins.codeowners.widget


import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent



class CodeOwnersWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.TextPresentation {
    override fun ID() = "org.brex.plugins.codeowners.CodeOwnersWidget"

    override fun getTooltipText(): String? = "Tooltop!"

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getPresentation() = this

    override fun install(statusBar: StatusBar) {
        //
    }

    override fun dispose() {
        //
    }

    override fun getAlignment() = Component.CENTER_ALIGNMENT

    override fun getText() = "Meep"
}
