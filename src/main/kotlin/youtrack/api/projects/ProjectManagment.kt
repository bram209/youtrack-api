package youtrack.api.projects

import youtrack.api.Managment
import youtrack.api.Youtrack
import youtrack.api.projects.Project



/**
 * Created by bram on 12/7/16.
 */
class ProjectManagment(context: Youtrack) : Managment(context) {

    fun list(): Array<ProjectNonVerbose> {
        return youtrack.get("project/all") ?: return arrayOf()
    }

    fun get(projectName: String): Project? {
        return youtrack.get("admin/project/$projectName")
    }

    fun priorities(): Array<ProjectPriority> {
        return youtrack.get("project/priorities") ?: return arrayOf()
    }

    //project/states

    fun states(): Array<ProjectState> {
        return youtrack.get("project/states") ?: return arrayOf()
    }




}