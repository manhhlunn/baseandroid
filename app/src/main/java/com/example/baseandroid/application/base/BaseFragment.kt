package com.example.baseandroid.application.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding

typealias MyFragment = BaseFragment<*>
typealias MyActivity = BaseActivity<*>

abstract class BaseFragment<B : ViewBinding> : Fragment() {

    val fragmentScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    val permissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    lateinit var binding: B

    private var view: ViewBinding? = null

    var isVisibleTabbar: Boolean = false

    var shouldReloadView: Boolean = false

    val mActivity: MyActivity?
        get() = this.activity as? MyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
//        if ((parentFragment == null || parentFragment is TabbarFragment ) && isVisibleTabbar) {
//        if (isVisibleTabbar) {
//            binding.root.setPaddingAsDP(bottom = 68)
//        } else {
//            binding.root.setPaddingAsDP()
//        }
//
//        mActivity?.showTabbar(isVisibleTabbar)
    }

    open fun setupView() {}

    open fun makeViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (shouldReloadView) {
            makeViewBinding(inflater, container, savedInstanceState)
            setupView()
            return binding.root
        }
        if (view == null) {
            makeViewBinding(inflater, container, savedInstanceState)
            view = binding
            setupView()
        }

        return this.binding.root
    }

    open fun onPermissionGranted() {}
    open fun onPermissionDenied() {}

    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancelCoroutines()
    }

    fun pushTo(@IdRes resId: Int, args: Bundle? = null) {
        mActivity?.pushTo(resId, args)
    }

    fun pushFadeTo(@IdRes resId: Int, args: Bundle? = null) {
        mActivity?.pushFadeTo(resId, args)
    }

    fun popTo(@IdRes destinationId: Int?, inclusive: Boolean = false) {
        mActivity?.popTo(destinationId, inclusive)
    }

    fun popToRoot() {
        mActivity?.popToRoot()
    }
}

abstract class BaseVMFragment<V : BaseViewModel, B : ViewBinding> : BaseFragment<B>() {
    abstract val viewModel: V
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isShowProgress.observe(this) { isShow ->
            (mActivity as? BaseVMActivity<*, *>)?.viewModel?.isShowProgress?.postValue(isShow)
        }
    }
}