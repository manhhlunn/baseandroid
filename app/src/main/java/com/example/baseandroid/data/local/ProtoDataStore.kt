package com.example.baseandroid.data.local

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.baseandroid.data.response.RefreshTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

val Context.refreshTokenResponseStore by dataStore(
    "refresh-token.json",
    BaseSerializer(RefreshTokenResponse("", ""), RefreshTokenResponse.serializer())
)


class BaseSerializer<T : Any>(private val default: T, private val kSerializer: KSerializer<T>) :
    Serializer<T> {

    override val defaultValue: T
        get() = default

    override suspend fun readFrom(input: InputStream): T {
        return try {
            Json.decodeFromString(kSerializer, input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(kSerializer, t).encodeToByteArray())
        }
    }
}


