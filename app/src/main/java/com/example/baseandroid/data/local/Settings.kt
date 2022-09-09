package com.example.baseandroid.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.baseandroid.application.MainApplication

class SharedPref private constructor(context: Context) {
    private val sharedPref: SharedPreferences

    companion object {
        private const val SHARED_NAME = "TEST_APP"
        private const val TOKEN = "TOKEN"

        private var mInstance: SharedPref? = null

        @Synchronized
        fun getInstance(): SharedPref {
            if (mInstance == null) {
                mInstance = SharedPref(MainApplication.CONTEXT)
            }
            return mInstance!!
        }
    }

    init {
        sharedPref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
    }

    fun removeAllData() {
        sharedPref.edit().clear().apply()
    }

    fun getToken(): String? {
        return sharedPref.getString(TOKEN, null)
    }

    fun setToken(token: String) {
        sharedPref.edit { this.putString(TOKEN, token) }
    }
}