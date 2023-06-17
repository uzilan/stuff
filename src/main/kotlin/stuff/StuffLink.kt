package stuff

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class StuffLink(private val client: OkHttpClient, private val url: String) {
    private val objectMapper = ObjectMapper()

    fun sendStuff(stuff: Stuff): Response {
        val request = Request.Builder()
            .url(url)
            .post(
                objectMapper.writeValueAsString(stuff)
                    .toRequestBody("application/json".toMediaType()),
            ).build()

        return client.newCall(request).execute()
    }
}
