package com.example.baseandroid.resource.injection

import android.content.Context
import com.example.baseandroid.BuildConfig
import com.example.baseandroid.data.local.DataStoreManager
import com.example.baseandroid.data.network.APIPath
import com.example.baseandroid.data.network.APIRequest
import com.example.baseandroid.data.network.ApiResponse
import com.example.baseandroid.data.network.ApiRouter
import com.example.baseandroid.data.network.ApiServiceWithoutToken
import com.example.baseandroid.data.network.HTTPError
import com.example.baseandroid.data.network.HTTPMethod
import com.example.baseandroid.data.network.getMethodCall
import com.example.baseandroid.data.network.toRequest
import com.example.baseandroid.data.request.RefreshTokenRequest
import com.example.baseandroid.data.response.RefreshTokenResponse
import com.example.baseandroid.resource.utils.ResultResponse
import com.example.baseandroid.resource.utils.isInternetAvailable
import com.example.baseandroid.resource.utils.safeLet
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Invocation
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WithoutAuthInterceptorClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @AuthInterceptorClient
    @Provides
    fun provideAuthClient(
        authInterceptor: AuthInterceptor,
        clientBuilder: OkHttpClient.Builder
    ): OkHttpClient = clientBuilder.apply {
        addInterceptor(authInterceptor)
    }.build()


    @WithoutAuthInterceptorClient
    @Provides
    fun provideWithoutAuthClient(
        clientBuilder: OkHttpClient.Builder
    ): OkHttpClient = clientBuilder.build()

    @Provides
    fun provideAuthClientBuilder(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient.Builder = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
        addInterceptor(networkConnectionInterceptor)
        connectTimeout(25, TimeUnit.SECONDS)
        callTimeout(25, TimeUnit.SECONDS)
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}

class AuthInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val withoutToken: ApiServiceWithoutToken
) : Interceptor {
    private val mutex = Mutex()
    private var isRefreshToken = false

    override fun intercept(chain: Interceptor.Chain): Response {

        val req = chain.request()
        val res = chain.proceedWithToken(req, dataStoreManager.accessToken)

        if (res.code != HTTPError.UNAUTHORIZED.code) {
            return res
        }

        if (res.code == HTTPError.UNAUTHORIZED.code) {
            isRefreshToken = false
        }

        val newToken: String? = runBlocking {
            mutex.withLock {
                try {
                    if (isRefreshToken) return@runBlocking dataStoreManager.accessToken
                    else when (val refreshToken = dataStoreManager.refreshToken) {
                        "" -> {
                            null
                        }

                        else -> {
                            val router = ApiRouter(
                                APIPath.refreshToken(), HTTPMethod.POST,
                                parameters =
                                RefreshTokenRequest(
                                    refreshToken
                                ).toRequest()
                            )

                            val response: retrofit2.Response<ApiResponse<RefreshTokenResponse>> =
                                withoutToken.getMethodCall(router)
                            if (response.isSuccessful) {
                                val value = response.body()
                                safeLet(
                                    value?.data?.access_token,
                                    value?.data?.refresh_token
                                ) { access_token, refresh_token ->
                                    dataStoreManager.accessToken = access_token
                                    dataStoreManager.refreshToken = refresh_token
                                    isRefreshToken = true
                                    return@runBlocking access_token
                                } ?: return@runBlocking null
                            } else {
                                return@runBlocking null
                            }
                        }
                    }
                } catch (e: Exception) {
                    return@runBlocking null
                }
            }
        }

        return if (newToken != null) {
            res.close()
            chain.proceedWithToken(req, newToken)
        } else {
            res
        }
    }


    private fun Interceptor.Chain.proceedWithToken(req: Request, token: String): Response =
        req.newBuilder()
            .apply {
                addHeader("JWTAuthorization", token)
            }
            .build()
            .let(::proceed)
}

class NetworkConnectionInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isInternetAvailable()) {
            throw NoConnectivityException()
            // Throwing our custom exception 'NoConnectivityException'
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No Internet Connection"
}