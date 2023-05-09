package com.example.baseandroid.data.network

import com.example.baseandroid.resource.utils.ResultResponse
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

data class ApiResponse<T>(
    @SerializedName("message")
    var message: String?,

    @SerializedName("status_code")
    var status_code: Int?,

    @SerializedName("data")
    var data: T?
)

data class CommonData(

    @SerializedName("areas") var areas: ArrayList<Any>? = arrayListOf(),
    @SerializedName("free_characters") var freeCharacters: ArrayList<Any> = arrayListOf()

) : Serializable

//Request
fun APIRequest.getShopInfo(id: Int): Flow<ResultResponse<ApiResponse<String>>> = request(ApiRouter(APIPath.shopInfo(id), parameters = String.toRequest()),true)

fun APIRequest.getList(page: Int): Flow<ResultResponse<ApiResponse<List<String>>>> = request(ApiRouter(APIPath.shopInfo(page), parameters = String.toRequest()),true)


//extension
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
    mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> = HashMap(this)

fun <T : Any> T.toRequest(): Parameters {
    return toMap(this).filterNotNullValues().toHashMap()
}

fun <T : Any> toMap(obj: T): Map<String, Any?> {
    return (obj::class as KClass<T>).memberProperties.associate { prop ->
        prop.name to prop.get(obj)?.let { value ->
            if (value::class.isData) {
                toMap(value)
            } else {
                value
            }
        }
    }
}

