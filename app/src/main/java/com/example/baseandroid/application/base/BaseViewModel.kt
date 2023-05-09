package com.example.baseandroid.application.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.example.baseandroid.resource.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    val isShowProgress: LiveData<Boolean> get() = _isShowProgress
    private val _isShowProgress = SingleLiveEvent<Boolean>()

    val handleException: LiveData<HttpException> get() = _handleException
    private val _handleException = SingleLiveEvent<HttpException>()

    fun onHandleException(httpException: HttpException) {
        _handleException.postValue(httpException)
    }

    fun showProgress() {
        if (_isShowProgress.value == true) {
            return
        }
        _isShowProgress.postValue(true)
    }

    fun hideProgress() {
        if (_isShowProgress.value == false) {
            return
        }
        _isShowProgress.postValue(false)
    }
}