package org.brex.plugins.codeowners.services

import com.intellij.openapi.project.Project
import org.brex.plugins.codeowners.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
