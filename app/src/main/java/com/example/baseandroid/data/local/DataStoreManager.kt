package com.example.baseandroid.data.local

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.baseandroid.BuildConfig
import com.example.baseandroid.data.network.fromJson
import com.example.baseandroid.data.response.BaseResponse
import com.example.baseandroid.data.response.RefreshTokenResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


private val Context.dataStore by preferencesDataStore(BuildConfig.APPLICATION_ID)

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context, gson: Gson) {

    val dataStore = appContext.dataStore
    var accessToken by myPreferenceDataStore("")
    var refreshToken by myPreferenceDataStore("")
    var example by myPreferenceDataStore(Example.FIRST)
    var response: BaseResponse? by myPreferenceDataStore(gson)

    inline fun <reified T> get(name: String, default: T) = runBlocking {
        when (default) {
            is String -> dataStore.data.first()[stringPreferencesKey(name)] ?: default
            is Int -> dataStore.data.first()[intPreferencesKey(name)] ?: default
            is Boolean -> dataStore.data.first()[booleanPreferencesKey(name)] ?: default
            is Double -> dataStore.data.first()[doublePreferencesKey(name)] ?: default
            is Float -> dataStore.data.first()[floatPreferencesKey(name)] ?: default
            is Long -> dataStore.data.first()[longPreferencesKey(name)] ?: default
            else -> throw IOException("Not support data type ${T::class.java}")
        } as? T ?: throw IOException("Not support data type ${T::class.java}")
    }

    inline fun <reified T> set(
        name: String,
        value: T,
    ) = runBlocking<Unit> {
        dataStore.edit {
            when (value) {
                is String -> it[stringPreferencesKey(name)] = value
                is Int -> it[intPreferencesKey(name)] = value
                is Boolean -> it[booleanPreferencesKey(name)] = value
                is Double -> it[doublePreferencesKey(name)] = value
                is Float -> it[floatPreferencesKey(name)] = value
                is Long -> it[longPreferencesKey(name)] = value
                else -> throw IOException("Not support data type ${T::class.java}")
            }
        }
    }
}

enum class Example {
    FIRST,
    SECOND,
    THIRD
}


inline fun <reified T : Any> myPreferenceDataStore(
    defaultValue: T,
) = object : ReadWriteProperty<DataStoreManager, T> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): T {
        return thisRef.get(name = property.name, default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: T) {
        thisRef.set(name = property.name, value = value)
    }
}

inline fun <reified T : Enum<T>> myPreferenceDataStore(
    defaultValue: T,
) = object : ReadWriteProperty<DataStoreManager, T> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): T {
        val stringEnum = thisRef.get(
            name = property.name,
            default = defaultValue.name
        )
        return enumValues<T>().first { it.name == stringEnum }
    }

    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: T) {
        thisRef.set(name = (property.name), value = value.name)
    }
}

inline fun <reified T> myPreferenceDataStore(
    gson: Gson
) = object : ReadWriteProperty<DataStoreManager, T?> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): T? {
        return try {
            val json = thisRef.get(
                name = property.name,
                default = "{}"
            )
            return gson.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: T?) {
        val json = gson.toJson(value)
        thisRef.set(name = (property.name), value = json)
    }
}








