# Retrofit API Implementation In Kotlin

- Retrofit API Implementation In Kotlin 


add dependency in your app root folder build.gradle file

```
// https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")

dependencies{
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
}
```

- Imports:

```
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.http.GET

```

- Create a client

```
val CLIENT: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(TagInterceptor())
}.build()
```

- Provide a base url:

```
const val BASE_URL = "" // Provide your base url
```

- Create a builder class

```
val SERVER_API: ServerApi =
    Retrofit.Builder().client(CLIENT).baseUrl(BASE_URL).build().create(ServerApi::class.java)
```

- Create an interface that will be available to make a call

```
interface ServerApi {

    @GET("api/notifications")
    @Tag("notifications")
    suspend fun getNotifications(): ResponseBody
}
```

### Optionals

- Set Annotations:

```
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tag(val value: String)
```

- Create an interceptor

```
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
```

- In case you want to cancel all the request in bwtween call made, Cancel using below

```
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
```

- Example:

```
// implementation

suspend fun main() {
    val notification = SERVER_API.getNotifications()
}
```
