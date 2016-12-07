package youtrack.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import youtrack.api.projects.Project

/**
 * Created by bram on 12/7/16.
 */
class Youtrack(val baseUrl: String) {

    private var authenticationCookie: String? = null

    fun authenticate(username: String, password: String) {
        val loginUrl = baseUrl + "/rest/user/login"
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
        val loginUrl = baseUrl + "/rest/project/all"
        val client = HttpClientBuilder.create().build()

        val get = HttpGet(loginUrl)
        get.addHeader("Cookie", authenticationCookie)
        get.addHeader("Accept", "application/json")
        val response = client.execute(get)
        if (response.statusLine.statusCode == 200) { //OK
            val mapper = jacksonObjectMapper()
            val projectList = mapper.readValue(response.entity.content, Array<Project>::class.java) ?: return arrayOf()
            return projectList
        } else {
            throw RuntimeException("Invalid response: $response")
        }
    }
}