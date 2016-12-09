package youtrack.api

import youtrack.api.issues.IssueManagment
import youtrack.api.projects.ProjectManagment

/**
 * Created by bram on 12/8/16.
 */
class MethodContext(youtrack: Youtrack) {

    val projectManagement: ProjectManagment
    val issueManagment: IssueManagment

    init {
        projectManagement = ProjectManagment(youtrack)
        issueManagment = IssueManagment(youtrack)
    }
}