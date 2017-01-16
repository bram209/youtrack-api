package youtrack.api.issues

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * Created by bram on 12/9/16.
 */
@JsonDeserialize(using = Link.Deserializer::class)
class Link(val target: String, val type: String, val role: String) {
    object Deserializer : JsonDeserializer<Link>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Link {
            val root = p!!.codec.readTree<ObjectNode>(p)
            if (root.has("typeInward")) {
                return Link(root.get("target").asText(),
                        root.get("typeName").asText(),
                        root.get("typeOutward").asText())
            }

            return Link(root.get("value").asText(),
                    root.get("type").asText(),
                    root.get("role").asText())
        }
    }
}

/*
 {
          "value": "TEST-2",
          "type": "Relates",
          "role": "relates to"
        },
 */

/*
   {
          "typeInward": "relates to",
          "typeOutward": "relates to",
          "typeName": "Relates",
          "target": "TEST-2",
          "source": "TEST-1"
        },
        {
 */