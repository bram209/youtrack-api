package youtrack.api.issues.fields

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import youtrack.api.issues.Attachment

/**
 * Created by bram on 12/8/16.
 */

//@JsonFormat(with = arrayOf(JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY))
@JsonDeserialize(using = AttachmentField.Deserializer::class)
class AttachmentField(attachments: Array<Attachment>) : AbstractField<Array<Attachment>>("attachment", attachments) {

    class Deserializer : StdDeserializer<AttachmentField>(AttachmentField::class.java) {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AttachmentField {
             /*
              "name": "attachments",
              "value": [
                {
                  "value": "Product View.png",
                  "id": "92-0",
                  "url": "http://laptop-bram:8080/_persistent/Product%20View.png?file=92-0&c=true"
                }
              ]
              */
            val root = p!!.codec.readTree<JsonNode>(p)
            val arrayNode = root["value"] as ArrayNode
            val list = arrayListOf<Attachment>()
            arrayNode.forEach {
                if (it.has("value")) {
                    list.add(Attachment(it.get("id").asText(), it.get("url").asText(), it.get("value").asText(), null, null, null))
                } else {
                    list.add(Attachment(it.get("id").asText(), it.get("url").asText(), it.get("name").asText(),
                            it.get("authorLogin").asText(), it.get("group").asText(), it.get("created").asLong()))
                }
            }

            /*
             arrayNode.forEach {
                val objectMapper = p.codec
                val attachment = objectMapper.treeToValue(it, Attachment::class.java)
                list.add(attachment)
            }
             */
            return AttachmentField(list.toTypedArray())
        }
    }
}

