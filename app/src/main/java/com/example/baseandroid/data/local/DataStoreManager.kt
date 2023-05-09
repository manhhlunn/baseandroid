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
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore
    var accessToken by myPreferenceDataStore("")
    var refreshToken by myPreferenceDataStore("")
    var example by myPreferenceDataStore(Example.FIRST)

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

fun myPreferenceDataStore(
    defaultValue: String,
) = object : ReadWriteProperty<DataStoreManager, String> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): String {
        return thisRef.get(key = stringPreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: String) {
        thisRef.set(key = stringPreferencesKey(property.name), value = value)
    }
}
fun myPreferenceDataStore(
    defaultValue: Int,
) = object : ReadWriteProperty<DataStoreManager, Int> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): Int {
        return thisRef.get(key = intPreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: Int) {
        thisRef.set(key = intPreferencesKey(property.name), value = value)
    }
}

fun myPreferenceDataStore(
    defaultValue: Boolean,
) = object : ReadWriteProperty<DataStoreManager, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): Boolean {
        return thisRef.get(key = booleanPreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: Boolean) {
        thisRef.set(key = booleanPreferencesKey(property.name), value = value)
    }
}

fun myPreferenceDataStore(
    defaultValue: Long,
) = object : ReadWriteProperty<DataStoreManager, Long> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): Long {
        return thisRef.get(key = longPreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: Long) {
        thisRef.set(key = longPreferencesKey(property.name), value = value)
    }
}

fun myPreferenceDataStore(
    defaultValue: Double,
) = object : ReadWriteProperty<DataStoreManager, Double> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): Double {
        return thisRef.get(key = doublePreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: Double) {
        thisRef.set(key = doublePreferencesKey(property.name), value = value)
    }
}

fun myPreferenceDataStore(
    defaultValue: Float,
) = object : ReadWriteProperty<DataStoreManager, Float> {

    @WorkerThread
    override fun getValue(thisRef: DataStoreManager, property: KProperty<*>): Float {
        return thisRef.get(key = floatPreferencesKey(property.name), default = defaultValue)
    }


    override fun setValue(thisRef: DataStoreManager, property: KProperty<*>, value: Float) {
        thisRef.set(key = floatPreferencesKey(property.name), value = value)
    }
}

inline fun <reified T : Enum<T>> myPreferenceDataStore(
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







