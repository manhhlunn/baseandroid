package com.example.baseandroid.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun dpFromPx(px: Int): Float {
    return px / Resources.getSystem().displayMetrics.density
}

