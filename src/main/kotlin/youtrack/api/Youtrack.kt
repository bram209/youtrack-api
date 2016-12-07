package youtrack.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import youtrack.api.projects.Project
import kotlin.reflect.KClass

/**
 * Created by bram on 12/7/16.
 */
class Youtrack(baseUrl: String) {
    private val restUrl: String = baseUrl + "/rest"
    private val loginUrl = restUrl + "/user/login"

    private val client = HttpClientBuilder.create().build()
    private var authenticationCookie: String? = null

    fun authenticate(username: String, password: String) {
        val client = HttpClientBuilder.create().build()

        val entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addTextBody("login", username).addTextBody("password", password)

        val entity = entityBuilder.build()
        val post = HttpPost(loginUrl)
        post.entity = entity

        val response = client.execute(post)
        client.close();
        if (response.statusLine.statusCode == 200) { //OK
            authenticationCookie = response.getHeaders("Set-Cookie")[0].value
            println(authenticationCookie)
        } else {
            throw RuntimeException("Invalid response: $response")
        }
    }

    fun projects(): Array<Project> {
        return get<Array<Project>>("/project/all") ?: return arrayOf()
    }

    private inline fun <reified T: Any> get(command: String): T? {
        val commandUrl = restUrl + command
        val httpGet = HttpGet(commandUrl)
        httpGet.addHeader("Cookie", authenticationCookie)
        httpGet.addHeader("Accept", "application/json")
        val response = client.execute(httpGet)
        if (response.statusLine.statusCode == 200) { //OK
            val mapper = jacksonObjectMapper()
            val projectList: T = mapper.readValue(response.entity.content, T::class.java)
            return projectList
        }

        return null
    }


}