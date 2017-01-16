package youtrack.api.issues

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.FileOutputStream


/**
 * Created by bram on 12/9/16.
 */

@JsonDeserialize(using = Attachment.Deserializer::class)
class Attachment(val id: String, val url: String, val name: String,
                 val authorLogin: String?, val group: String?, val created: Long?) {

    fun download() {
        val outputFile = File(name)
        downloadToFile(outputFile)
    }

    fun downloadToDir(outputDir: File) {
        val outputFile = File(outputDir, name)
        downloadToFile(outputFile)
    }

    fun downloadToFile(outputFile: File) {
        val client = HttpClientBuilder.create().build()
        val httpGet = HttpGet(url + "/")
        val response = client.execute(httpGet)
        println(response.entity.contentType)
        println(EntityUtils.toString(response.entity))
        FileOutputStream(outputFile).use({ outstream -> response.entity.writeTo(outstream) })
    }

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