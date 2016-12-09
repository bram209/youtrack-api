package youtrack.api.issues

/**
 * Created by bram on 12/8/16.
 */
class Comment(val id: String, val author: String, val authorFullName: String, val issueId: String,
              val parentId: String?, val deleted: Boolean, val jiraId:String?,
              val text: String, val shownForIssueAuthor: Boolean, created: Long, updated: Long,
              val permittedGroup: String?, val replies: Array<String>)