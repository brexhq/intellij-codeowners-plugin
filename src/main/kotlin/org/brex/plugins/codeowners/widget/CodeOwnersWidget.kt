package org.brex.plugins.codeowners.widget


import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent



class CodeOwnersWidget : StatusBarWidget, StatusBarWidget.TextPresentation {
    init {
        println("YOU WHAT MATEEEEEYYY")
    }

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

    override fun getText() = "Codeowner!!"
}
