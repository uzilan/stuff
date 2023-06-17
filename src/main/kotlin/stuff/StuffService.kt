package stuff

import okhttp3.OkHttpClient

class StuffService() {
    private val client = OkHttpClient()
        .newBuilder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }.build()

    private val stuffLink = StuffLink(client, "http://some.where")

    fun sendStuff(stuff: Stuff) = stuffLink.sendStuff(stuff)
}
