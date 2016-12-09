package youtrack.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode


/**
 * Created by bram on 12/9/16.
 */
open class PropertyBasedPolymorphicDeserializer<T : Any>(clazz: Class<T>, val propertyName: String) : StdDeserializer<T>(clazz) {

    private val registeredValues = hashMapOf<Any, Class<*>>()
    private val registeredConditions = arrayListOf<Pair<(Any) -> Boolean, Class<*>>>()

    fun registerClassBasedOnValue(value: Any, clazz: Class<*>) {
        registeredValues[value] = clazz
    }

    fun registerClassBasedOnCondition(condition: (Any) -> Boolean, clazz: Class<*>) {
        registeredConditions.add(Pair(condition, clazz))
    }

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T {
        val mapper = p!!.codec as ObjectMapper
        val obj: ObjectNode = mapper.readTree(p)
        println(obj)
        var clazz: Class<*>? = null

        val value = obj[propertyName]
        println(value)
        if (value == null) {
            ctxt!!.mappingException("No property with name: $propertyName")
        }

        if (registeredValues.containsKey(value)) {
            clazz = registeredValues[value]
        } else {
            for (pair in registeredConditions) {
                val (cond, c) = pair
                if (cond.invoke(value)) {
                    clazz = c
                    break
                }
            }
        }

        if (clazz == null) {
            ctxt!!.mappingException("No class found for property value: $value")
        }

        println(clazz)
        return mapper.treeToValue(obj, clazz) as T
    }
}