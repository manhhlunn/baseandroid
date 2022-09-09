package com.example.baseandroid.data.network

import com.example.baseandroid.BuildConfig
import com.example.baseandroid.data.network.APIRequest.Companion.BASE_URL
import com.example.gurume_go_android.data.network.ApiService
import com.example.gurume_go_android.data.network.Headers
import com.example.gurume_go_android.data.network.Parameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

object APIPath {
    fun shopInfo(id: Int): String = "shop/${id}"
}

enum class HTTPError(val code: Int) {
    UNAUTHORISE(401)
}

@Singleton
class APIRequest @Inject constructor(
    val service: ApiService,
    val gson: Gson
) {

    companion object {
        const val BASE_URL = BuildConfig.API_ENDPOINT
    }

    suspend inline fun <reified T> request(
        router: ApiRouter
    ): Result<T> {
        return try {
            val body = when (router.method) {
                HTTPMethod.GET -> service.get(router.url(), router.headers, router.parameters)
                HTTPMethod.POST -> service.post(router.url(), router.headers, router.parameters)
                HTTPMethod.PUT -> service.put(router.url(), router.headers, router.parameters)
                HTTPMethod.DELETE -> service.delete(router.url(), router.headers, router.parameters)
            }
            return Result.success(gson.fromJson(body.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

//suspend fun Result<*>.onRetry(complete: () -> Unit) {
//    onFailure {
//        if (MainApplication.CONTEXT?.showTwoActionAlert(
//                "エラーが発生しました",
//                "もう一度やり直してください",
//                "リトライ",
//                "キャンセル"
//            ) == true
//        ) {
//            complete()
//        }
//    }
//}

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

data class ApiRouter(
    val path: String,
    val method: HTTPMethod = HTTPMethod.GET,
    val parameters: Parameters = hashMapOf(),
    val headers: Headers = JsonFormatter
)

val JsonFormatter = hashMapOf("accept" to "application/json", "Content-Type" to "application/json")

fun ApiRouter.url(): String = BASE_URL + path

enum class HTTPMethod {
    GET, POST, PUT, DELETE
}

inline fun Throwable.httpCode(): Int = (this as HttpException).code()