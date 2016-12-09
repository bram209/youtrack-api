package youtrack.api.projects

import com.sun.org.apache.xpath.internal.operations.Bool

/**
 * Created by bram on 12/7/16.
 */
class ProjectVersion(var name: String, var description: String, var colorIndex: Int?, var releaseData: Long?, var released: Boolean?, var archived: Boolean?) {
}