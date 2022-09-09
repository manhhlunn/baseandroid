package com.example.baseandroid.view.main_activity

import android.os.Bundle
import androidx.activity.viewModels
import com.example.baseandroid.application.base.BaseActivity
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.data.network.APIRequest
import com.example.baseandroid.databinding.ActivityMainTabbarBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainTabbarBinding>() {

    override val viewModel: MainViewModel by viewModels()

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
    }

    override fun makeViewBinding() {
        super.makeViewBinding()
        binding = ActivityMainTabbarBinding.inflate(layoutInflater)
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(val apiRequest: APIRequest) :
    BaseViewModel() {
}



