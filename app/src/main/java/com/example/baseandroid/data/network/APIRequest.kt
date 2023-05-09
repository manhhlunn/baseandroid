package com.example.baseandroid.data.network

import com.example.baseandroid.BuildConfig
import com.example.baseandroid.resource.utils.ResultResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

object APIPath {
    fun shopInfo(id: Int): String = "shop/${id}"
    fun common(): String = "common"
    fun refreshToken() = "refreshToken"
}

enum class HTTPError(val code: Int) {
    UNAUTHORIZED(401),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    PERMISSION_DENIED(406),
    MAINTENANCE(407),
    VERSION_UPGRADE(427),
    SERVER_ERROR(500)
}

@Singleton
class APIRequest @Inject constructor(
    private val service: ApiService,
    private val serviceWithoutToken: ApiServiceWithoutToken
) {

    companion object {
        const val BASE_URL = BuildConfig.API_ENDPOINT
    }

    inline fun <reified T> request(
        router: ApiRouter,
        withToken: Boolean
    ): Flow<ResultResponse<T>> = flow {
        try {
            val response: Response<T> = getService(withToken).getMethodCall(router)
            val value = response.body()
            if (response.isSuccessful && value != null) {
                emit(ResultResponse.Success(value))
            } else {
                val ex = HttpException(response)
                emit(ex.filterError())
            }
        } catch (e: HttpException) {
            emit(e.filterError())
        } catch (e: Exception) {
            emit(ResultResponse.Error(e))
        }
    }

    fun getService(needLogin: Boolean): BaseApiService {
        return if (needLogin) service else serviceWithoutToken
    }
}

val DEFAULT_CODE = listOf(
    HTTPError.NOT_FOUND,
    HTTPError.MAINTENANCE,
    HTTPError.SERVER_ERROR,
    HTTPError.VERSION_UPGRADE,
    HTTPError.PERMISSION_DENIED
)

fun HttpException.filterError() = if (DEFAULT_CODE.map { it.code }
        .contains(httpCode())) ResultResponse.DefaultError(this) else ResultResponse.Error(this)


suspend fun <T> BaseApiService.getMethodCall(router: ApiRouter): Response<T> =
    when (router.method) {
        HTTPMethod.GET -> get(router.url(), router.headers, router.parameters)
        HTTPMethod.POST -> post(router.url(), router.headers, router.parameters)
        HTTPMethod.PUT -> put(router.url(), router.headers, router.parameters)
        HTTPMethod.DELETE -> delete(router.url(), router.headers, router.parameters)
    }


inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

data class ApiRouter(
    val path: String,
    val method: HTTPMethod = HTTPMethod.GET,
    val parameters: Parameters = hashMapOf(),
    val headers: Headers = JsonFormatter
)

val JsonFormatter = hashMapOf("accept" to "application/json", "Content-Type" to "application/json")

fun ApiRouter.url(): String = APIRequest.BASE_URL + path

enum class HTTPMethod {
    GET, POST, PUT, DELETE
}

fun Throwable.httpCode(): Int = (this as HttpException).code()