package youtrack.api.issues

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import youtrack.api.MethodContext
import youtrack.api.issues.fields.AbstractField


/**
 * Created by bram on 12/8/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonDeserialize(converter = Issue.OurConverter::class, using = Issue.Deserializer::class)
//@JsonDeserialize(using = Issue.Deserializer::class)
class Issue(@JacksonInject val context: MethodContext,
            val id: String,
            val entityId: String?,
            val jiraId: String?,
            @JsonProperty("projectShortName") val projectName: String,
            val numberInProject: Int,
            @JsonProperty("type") private val _type: String?,
            val summary: String,
            val description: String?,
            val created: Long,
            val updated: Long,
            val updaterName: String,
            val resolved: Boolean,
            val reporterName: String,
            val commentsCount: Int,
            val votes: Int,
            val permittedGroup: String?,
            @JsonProperty("field") val fields: Array<AbstractField<*>>,
            @JsonProperty("links") private val links: Array<Link>,
            @JsonProperty("attachments") private var _attachments: Array<Attachment>?,
            @JsonProperty("comment") private var _comments: Array<Comment>?) {

    val fieldMap: Map<String, Any>

    init {
        fieldMap = hashMapOf()
        for (field in fields) {
            if (field.value != null) {
                fieldMap[field.name] = field.value
            }
        }
    }

    val type: String
        get() {
            if (_type != null) {
                return _type
            } else {
                return getField("Type") as String
            }
        }

    fun getField(fieldName: String): Any? {
        return fieldMap[fieldName]
    }

    fun comments(): Array<Comment> {
        if (_comments == null) {
            _comments = context.issueManagment.getCommentsForIssue(id)
        }

        return _comments!!
    }

    fun attachments(): Array<Attachment> {
        if (_attachments == null) {
            _attachments = context.issueManagment.getAttachmentsForIssue(id)
        }

        return _attachments!!
    }

    fun links(): Array<Link> {
        return links
    }

    object Deserializer : JsonDeserializer<Issue>() {
        val COMMON_FIELD_NAMES = setOf(
                "projectShortName", "numberInProject", "summary", "description", "created", "updated",
                "resolved", "updaterName", "updaterFullName", "reporterName", "commentsCount", "votes", "permittedGroup",
                "reporterFullName", "attachments", "links"
        )

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Issue {

            val root: ObjectNode = p!!.codec.readTree(p)

            val fields: ArrayNode = root.get("field") as ArrayNode
            val iterator = fields.iterator()
            while (iterator.hasNext()) {
                val fieldNode = iterator.next()
                val fieldName = fieldNode.get("name").asText()
                if (COMMON_FIELD_NAMES.contains(fieldName) && fieldNode.hasNonNull("value")) {
                    iterator.remove()
                    root.set(fieldName, fieldNode.get("value"))
                }
            }

            val mapper = jacksonObjectMapper()
            mapper.injectableValues = (p.codec as ObjectMapper).injectableValues
            return mapper.treeToValue(root, Issue::class.java)
        }
    }
}

