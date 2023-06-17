package stuff

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StuffLinkTest {
    private val objectMapper = ObjectMapper()
    private val url = "http://what.ever/"

    @Test
    fun `Stuff should be sent by StuffLink (version 1)`() {
        val requestSlot = slot<Request>()
        val mockkClient = mockk<OkHttpClient> {
            every { newCall(capture(requestSlot)) } returns mockk(relaxed = true)
        }

        val stuffLink = StuffLink(mockkClient, url)
        val bob = Stuff("Bob", "Armchair")
        stuffLink.sendStuff(bob)
        val request = requestSlot.captured
        val bodyAsMap = parseRequestBody(request)

        assertThat(request.url.toString()).isEqualTo(url)
        assertThat(bodyAsMap["name"]).isEqualTo("Bob")
        assertThat(bodyAsMap["type"]).isEqualTo("Armchair")
    }

    @Test
    fun `Stuff should be sent by StuffLink (version 2)`() {
        val (mockkClient, requestSlot) = createMockAndSlot()
        val stuffLink = StuffLink(mockkClient, url)
        val bob = Stuff("Bob", "Armchair")
        stuffLink.sendStuff(bob)
        val request = requestSlot.captured
        val bodyAsMap = parseRequestBody(request)

        assertThat(request.url.toString()).isEqualTo(url)
        assertThat(bodyAsMap["name"]).isEqualTo("Bob")
        assertThat(bodyAsMap["type"]).isEqualTo("Armchair")
    }

    @Test
    fun `Stuff should be sent by StuffLink (version 3)`() {
        val bob = Stuff("Bob", "Armchair")
        val request = runInStuffLink { sendStuff(bob) }
        val bodyAsMap = parseRequestBody(request)

        assertThat(request.url.toString()).isEqualTo(url)
        assertThat(bodyAsMap["name"]).isEqualTo("Bob")
        assertThat(bodyAsMap["type"]).isEqualTo("Armchair")
    }

    private fun parseRequestBody(request: Request): Map<String, Any> {
        val buffer = Buffer()
        request.body!!.writeTo(buffer)
        return objectMapper.readValue(buffer.readUtf8(), object : TypeReference<Map<String, Any>>() {})
    }

    private fun createMockAndSlot(): Pair<OkHttpClient, CapturingSlot<Request>> {
        val requestSlot = slot<Request>()
        val mockkClient = mockk<OkHttpClient> {
            every<Call> { newCall(capture(requestSlot)) } returns mockk<Call>(relaxed = true)
        }
        return mockkClient to requestSlot
    }

    private fun runInStuffLink(action: StuffLink.() -> Unit): Request {
        val requestSlot = slot<Request>()
        val mockkClient = mockk<OkHttpClient> {
            every<Call> { newCall(capture(requestSlot)) } returns mockk<Call>(relaxed = true)
        }
        val stuffLink = StuffLink(mockkClient, url)
        stuffLink.action()
        return requestSlot.captured
    }
}
