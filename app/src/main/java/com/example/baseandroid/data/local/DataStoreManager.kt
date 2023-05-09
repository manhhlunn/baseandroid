package com.example.baseandroid.data.local

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.baseandroid.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


private val Context.dataStore by preferencesDataStore(BuildConfig.APPLICATION_ID)

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore
    var accessToken by myPreferenceDataStore(stringPreferencesKey("ACCESS_TOKEN"), "")
    var refreshToken by myPreferenceDataStore(stringPreferencesKey("REFRESH_TOKEN"), "")
    var example by enumPreferenceDataStore(Example.FIRST)

    fun <T> get(key: Preferences.Key<T>, default: T): T = settingsDataStore.get(key, default)
    fun <T> set(key: Preferences.Key<T>, value: T) = settingsDataStore.set(key, value)

}

enum class Example {
    FIRST,
    SECOND,
    THIRD
}


fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>, default: T) = runBlocking {
    data.first()[key] ?: default
}

fun <T> DataStore<Preferences>.set(
    key: Preferences.Key<T>,
    value: T?,
) = runBlocking<Unit> {
    edit {
        if (value == null) {
            it.remove(key)
        } else {
            it[key] = value
        }
    }
}

inline fun <reified T> myPreferenceDataStore(
    key: Preferences.Key<T>, defaultValue: T,
) = object : ReadWriteProperty<DataStoreManager, T> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): T {
        return thisRef.get(key = key, default = defaultValue)
    }

    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: T) {
        thisRef.set(key = key, value = value)
    }
}

inline fun <reified T : Enum<T>> enumPreferenceDataStore(
    defaultValue: T,
) = object : ReadWriteProperty<DataStoreManager, T> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): T {
        val stringEnum = thisRef.get(
            key = stringPreferencesKey(property.name),
            default = defaultValue.name
        )
        return enumValues<T>().first { it.name == stringEnum }
    }

    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: T) {
        thisRef.set(key = stringPreferencesKey(property.name), value = value.name)
    }
}







