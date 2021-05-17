package org.brex.plugins.template.services

import com.intellij.openapi.project.Project
import org.brex.plugins.template.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
