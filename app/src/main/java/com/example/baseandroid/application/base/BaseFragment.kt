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
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

typealias MyFragment = BaseFragment<*, *>
typealias MyActivity = BaseActivity<*, *>

open class CoroutineLauncher : CoroutineScope {

    open val dispatcher: CoroutineDispatcher = Dispatchers.Main
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = dispatcher + supervisorJob

    fun launch(action: suspend CoroutineScope.() -> Unit) = launch(block = action)

    fun cancelCoroutines() {
        supervisorJob.cancelChildren()
        supervisorJob.cancel()
    }
}

abstract class BaseFragment<V : BaseViewModel, B : ViewBinding> : Fragment() {

    val fragmentScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    var myTag: String = this::class.java.simpleName

    val permissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    abstract val viewModel: V

    lateinit var binding: B

    private var view: ViewBinding? = null

    var isVisibleTabbar: Boolean = false

    var shouldReloadView: Boolean = false

    val mActivity: MyActivity?
        get() = this.activity as? BaseActivity<*, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isShowProgress.observe(this) { isShow ->
            mActivity?.viewModel?.isShowProgress?.postValue(isShow)
        }
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
        binding.root.findNavController().currentDestination?.getAction(resId)?.navOptions?.let {
            binding.root.findNavController().navigate(
                resId,
                args,
                navOptions { // Use the Kotlin DSL for building NavOptions
//                    anim {
//                        enter = R.anim.slide_in
//                        exit = R.anim.fade_out
//                        popEnter = R.anim.fade_in
//                        popExit = R.anim.slide_out
//                    }
                    popUpTo(it.popUpToId) {
                        inclusive = it.isPopUpToInclusive()
                    }
                }
            )
        }
    }

    fun pushFadeTo(@IdRes resId: Int, args: Bundle? = null) {
        binding.root.findNavController().currentDestination?.getAction(resId)?.navOptions?.let {
            binding.root.findNavController().navigate(
                resId,
                args,
                navOptions { // Use the Kotlin DSL for building NavOptions
                    anim {
//                    enter = R.anim.fade_in
//                    exit = R.anim.fade_out
                    }
                    popUpTo(it.popUpToId) {
                        inclusive = it.isPopUpToInclusive()
                    }
                }
            )
        }
    }

    fun popTo(@IdRes destinationId: Int?, inclusive: Boolean = false) {
        binding.root.findNavController().apply {
            if (destinationId == null) popBackStack()
            else popBackStack(destinationId, inclusive)
        }
    }
}