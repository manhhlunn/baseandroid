package com.example.baseandroid.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Url

typealias Parameters = HashMap<String, Any>
typealias Headers = HashMap<String, String>

interface ApiService : BaseApiService
interface ApiServiceWithoutToken : BaseApiService

interface BaseApiService {

    @GET
    suspend fun <T> get(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @QueryMap parameters: Parameters = hashMapOf()
    ): retrofit2.Response<T>

    @POST
    suspend fun <T> post(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @Body parameters: Parameters = hashMapOf()
    ): retrofit2.Response<T>

    @PUT
    suspend fun <T> put(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @Body parameters: Parameters = hashMapOf()
    ): retrofit2.Response<T>

    @DELETE
    suspend fun <T> delete(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @QueryMap parameters: Parameters = hashMapOf()
    ): retrofit2.Response<T>

    @Multipart
    @POST
    @JvmSuppressWildcards
    suspend fun <T> uploadFile(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @PartMap map: Map<String, RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): retrofit2.Response<T>

    @Multipart
    @POST
    @JvmSuppressWildcards
    suspend fun <T> uploadMultiFile(
        @Url url: String,
        @HeaderMap headers: Headers = hashMapOf(),
        @PartMap map: Map<String, RequestBody>,
        @Part images: List<MultipartBody.Part>?
    ): retrofit2.Response<T>
}
