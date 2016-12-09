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

    //API method providers
//    val projects = ProjectManagment(this)
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

    class WrapperDeserializer<T>(t: Class<T>, val defaultDeserializer: JsonDeserializer<*>?) : StdDeserializer<T>(t), ResolvableDeserializer {
        override fun resolve(ctxt: DeserializationContext?) {
            (defaultDeserializer as ResolvableDeserializer).resolve(ctxt)
        }

        override fun deserializeWithType(p: JsonParser?, ctxt: DeserializationContext?, typeDeserializer: TypeDeserializer?): Any {
            println("des with tye")
            return super.deserializeWithType(p, ctxt, typeDeserializer)
        }

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?, intoValue: T): T {
            println("test")
            return super.deserialize(p, ctxt, intoValue)
        }

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T {
            println("deserialize")

            return "" as T
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
//                mapper.registerModule(SimpleModule().setDeserializerModifier(object : BeanDeserializerModifier() {
//                    override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription?, deserializer: JsonDeserializer<*>?): JsonDeserializer<*> {
//                        if (T::class.java.componentType == beanDesc!!.beanClass) {
//                            return WrapperDeserializer(T::class.java.componentType, deserializer)
//                        }
//
//                        return deserializer!!
//                    }
//                }))
//                [{"priority":"3","type":"User Story","state":"Verified","subsystem":"No Subsystem","affectsVersion":null,"id":"FUNCRATE-1","fixedVersion":null,"projectShortName":"FUNCRATE","assigneeName":null,"reporterName":"Edwin","updaterName":"Edwin","fixedInBuild":"Next build","commentsCount":0,"numberInProject":1,"summary":"QR code","description":"As a user I want to be able to scan a QR code to connect.","created":1476268073341,"updated":1478702363630,"historyUpdated":1478702363630,"resolved":1478702363606,"jiraId":null,"votes":0,"permittedGroup":null,"field":[{"name":"Estimation","value":[],"valueId":[],"color":null}],"attachments":[],"links":[]},{"priority":"3","type":"User Story","state":"Verified","subsystem":"No Subsystem","affectsVersion":null,"id":"FUNCRATE-2","fixedVersion":null,"projectShortName":"FUNCRATE","assigneeName":null,"reporterName":"Edwin","updaterName":"Edwin","fixedInBuild":"Next build","commentsCount":0,"numberInProject":2,"summary":"Link","description":"As a user I want to be able to type in a link to connect.","created":1476268155706,"updated":1478702414494,"historyUpdated":1478702414494,"resolved":1478702414490,"jiraId":null,"votes":0,"permittedGroup":null,"field":[{"name":"Estimation","value":[],"valueId":[],"color":null}],"attachments":[],"links":[]}]

                val tree = mapper.readTree(response.entity.content)
                return mapper.treeToValue(tree.fields().next().value)
            }

            val projectList: T = mapper.readValue(response.entity.content, T::class.java)
            return projectList
        }

        return null
    }
}