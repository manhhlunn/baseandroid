package com.example.gurume_go_android.data.network

import com.example.baseandroid.data.network.APIPath
import com.example.baseandroid.data.network.APIRequest
import com.example.baseandroid.data.network.ApiRouter
import com.example.baseandroid.data.request.BaseRequest
import com.example.baseandroid.data.response.BaseResponse
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

//Request
suspend inline fun APIRequest.getShopInfo(id: Int, request: BaseRequest): Result<BaseResponse> =
    request(ApiRouter(APIPath.shopInfo(id), parameters = toMap(request).filterNotNullValues().toHashMap()))

//extension
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
    mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> = HashMap(this)

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