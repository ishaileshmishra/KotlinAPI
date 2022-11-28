package com.shaileshmishra.apilib

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.http.GET

val CLIENT: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(TagInterceptor())
}.build()

const val BASE_URL = "" // Provide your base url

val SERVER_API: ServerApi =
    Retrofit.Builder().client(CLIENT).baseUrl(BASE_URL).build().create(ServerApi::class.java)

interface ServerApi {

    @GET("api/notifications")
    @Tag("notifications")
    suspend fun getNotifications(): ResponseBody
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(val value: String)

internal class TagInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        request.tag(Invocation::class.java)?.let {
            it.method().getAnnotation(Tag::class.java)?.let { tag ->
                builder.tag(tag.value)
            }
        }
        return chain.proceed(builder.build())
    }
}

fun OkHttpClient.cancelAll(tag: String) {
    for (call in dispatcher().queuedCalls()) {
        if (tag == call.request().tag()) {
            call.cancel()
        }
    }
    for (call in dispatcher().runningCalls()) {
        if (tag == call.request().tag()) {
            call.cancel()
        }
    }
}


// implementation

suspend fun main() {
    val notification = SERVER_API.getNotifications()
}