package com.example.baseandroid.view.main_activity

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.baseandroid.R
import com.example.baseandroid.application.base.BaseVMActivity
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.application.base.ClearFocus
import com.example.baseandroid.application.base.ClearFocusImpl
import com.example.baseandroid.application.base.NavigationAction
import com.example.baseandroid.application.base.NavigationActionImpl
import com.example.baseandroid.data.local.DataStoreManager
import com.example.baseandroid.data.network.APIRequest
import com.example.baseandroid.data.network.getShopInfo
import com.example.baseandroid.databinding.ActivityMainTabbarBinding
import com.example.baseandroid.resource.utils.request
import com.example.baseandroid.resource.utils.setupActivityFullScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    BaseVMActivity<ActivityMainTabbarBinding, MainViewModel>(ActivityMainTabbarBinding::inflate),
    NavigationAction by NavigationActionImpl(),
    ClearFocus by ClearFocusImpl() {

    override val viewModel: MainViewModel by viewModels()

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initNavigation(this, R.id.nav_host_fragment)
        setupActivityFullScreen(binding.root, isHideStatusBar = true, isHideNavigationBar = false)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return onEventClearFocus(this, event, super.dispatchTouchEvent(event))
    }

}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRequest: APIRequest,
    private val dataStoreManager: DataStoreManager
) :
    BaseViewModel(),
    NavigationAction by NavigationActionImpl() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response


    fun getValue() {
        request(apiRequest.getShopInfo(0)) {

        }
    }
}

@HiltViewModel
class NavigationMainViewModel @Inject constructor() : ViewModel() {
    val liveData = MutableLiveData<String>()
}



