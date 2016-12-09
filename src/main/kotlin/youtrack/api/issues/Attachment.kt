package youtrack.api.issues

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Created by bram on 12/9/16.
 */

@JsonDeserialize(using = Attachment.Deserializer::class)
class Attachment(val id: String, val url: String, name: String,
                 val authorLogin: String?, val group: String?, val created: Long?) {

    object Deserializer : JsonDeserializer<Attachment>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Attachment {
            val node: JsonNode = p!!.codec.readTree(p)
            if (node.has("value")) {
                return Attachment(node.get("id").asText(), node.get("url").asText(), node.get("value").asText(), null, null, null)
            } else {
                return Attachment(node.get("id").asText(), node.get("url").asText(), node.get("name").asText(),
                        node.get("authorLogin").asText(), node.get("group").asText(), node.get("created").asLong())
            }
        }

    }

}