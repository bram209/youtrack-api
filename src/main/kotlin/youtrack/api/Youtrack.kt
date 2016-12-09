package youtrack.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClientBuilder
import java.lang.reflect.ParameterizedType
import com.sun.javafx.fxml.BeanAdapter.getGenericType
import youtrack.api.issues.Issue


/**
 * Created by bram on 12/7/16.
 */
class Youtrack(baseUrl: String) {
    val restUrl: String = baseUrl + "/rest"
    private val loginUrl = restUrl + "/user/login"

    val client = HttpClientBuilder.create().build()!!
    var authenticationCookie: String? = null

    val context = MethodContext(this)

    fun authenticate(username: String, password: String) {
        val entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addTextBody("login", username).addTextBody("password", password)

        val entity = entityBuilder.build()
        val post = HttpPost(loginUrl)
        post.entity = entity

        val response = client.execute(post)
        if (response.statusLine.statusCode == 200) { //OK
            authenticationCookie = response.getHeaders("Set-Cookie")[0].value
            println(authenticationCookie)
        } else {
            throw RuntimeException("Invalid response: $response")
        }
    }

    inline fun <reified T : Any> get(command: String, wrapped: Boolean = false): T? {
        val commandUrl = "$restUrl/$command"
        println(commandUrl)
        val httpGet = HttpGet(commandUrl)
        httpGet.addHeader("Cookie", authenticationCookie)
        httpGet.addHeader("Accept", "application/json")
        val response = client.execute(httpGet)
        if (response.statusLine.statusCode == 200) { //OK
//            println(EntityUtils.toString(response.entity))
            val mapper = jacksonObjectMapper()
            val inject = InjectableValues.Std().addValue(MethodContext::class.java, context)
            mapper.injectableValues = inject

            val deserializerModule = SimpleModule()
            deserializerModule.addDeserializer(Issue::class.java, Issue.Deserializer)
            mapper.registerModule(deserializerModule)
            if (wrapped) {
                val tree = mapper.readTree(response.entity.content)
                return mapper.treeToValue(tree.fields().next().value)
            }

            val projectList: T = mapper.readValue(response.entity.content, T::class.java)
            return projectList
        }

        return null
    }
}