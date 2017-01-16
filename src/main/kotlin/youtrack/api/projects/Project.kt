package youtrack.api.projects

import com.fasterxml.jackson.annotation.JacksonInject
import youtrack.api.MethodContext
import youtrack.api.issues.Issue

/**
 * Created by bram on 12/7/16.
 */
//@JsonIgnoreProperties(ignoreUnknown = true)

class ProjectNonVerbose(@JacksonInject val context: MethodContext, val name: String, val shortName: String) {
    fun toVerbose(): Project? {
        return context.projectManagement.get(name)
    }
}

class Project(@JacksonInject val context: MethodContext,
              val id: String,
              val name: String,
              val description: String,
              val lead: String,
              val archived: Boolean,
              val startingNumber: Int,
              val assigneesUrl: String,
              val subsystemsUrl: String,
              val buildsUrl: String,
              val versionsUrl: String) {

//    private var _issues : Array<Issue>? = null

    fun issues(max: Int = Int.MAX_VALUE, start: Int = 0): Array<Issue> {
//        if (_issues == null) {
        return context.issueManagment.getAllIssuesForProject(id, max, start)
    }
}