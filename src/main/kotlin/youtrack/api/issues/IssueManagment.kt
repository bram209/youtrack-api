package youtrack.api.issues

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import youtrack.api.Managment
import youtrack.api.Youtrack

/**
 * Created by bram on 12/8/16.
 */
@Suppress("ArrayInDataClass")
class IssueManagment(youtrack: Youtrack) : Managment(youtrack) {

    fun getIssue(issueId: String): Issue? {
        return youtrack.get("issue/$issueId")
    }

    fun getAllIssuesForProject(projectId: String, max: Int = Int.MAX_VALUE, start: Int = 0): Array<Issue> {
        return youtrack.get("project/issues/$projectId?max=$max&after=$start", true) ?: return arrayOf()
    }

    fun getCommentsForIssue(issueId: String): Array<Comment> {
        return youtrack.get("issue/comments/$issueId", true) ?: return arrayOf()
    }

    fun  getAttachmentsForIssue(issueId: String): Array<Attachment> {
        throw NotImplementedError()
    }
}