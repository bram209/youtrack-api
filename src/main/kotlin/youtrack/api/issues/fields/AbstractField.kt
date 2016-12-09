package youtrack.api.issues.fields

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import sun.net.www.content.text.Generic
import youtrack.api.PropertyBasedPolymorphicDeserializer

/**
 * Created by bram on 12/8/16.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "name",
        visible = true,
        defaultImpl = SingleValueField::class
)
@JsonSubTypes(
        JsonSubTypes.Type(value = AttachmentField::class, name = "attachments")
)
open class AbstractField<out T>(val name: String, val value: T)

@JsonIgnoreProperties(ignoreUnknown = true)
class SingleValueField(value: Any?) : AbstractField<Any?>("Test", value)