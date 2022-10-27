package com.example.baseandroid.application.base

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.R
import com.example.baseandroid.resource.customView.ProgressView

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    val activityScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    var progress: ProgressView? = null

    lateinit var binding: B

    var tabbar: View? = null
    var navContainer: NavController? = null

    @IdRes
    open var rootDes: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeViewBinding()
        setContentView(binding.root)
        setupView(savedInstanceState)
    }

    open fun makeViewBinding() {}

    open fun setupView(savedInstanceState: Bundle?) {}

//    fun showTabbar(isShow: Boolean) {
//        tabbar?.animate()
//            ?.setDuration(0)?.translationY(if (isShow) 0f else 200f)
//            ?.alpha(if (isShow) 1f else 0.0f)
//            ?.setComplete {
//                tabbar?.visibility = if (isShow) View.VISIBLE else View.GONE
//            }?.start()
//    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancelCoroutines()
    }

    fun pushTo(@IdRes resId: Int, args: Bundle? = null) {
        navContainer?.currentDestination?.getAction(resId)?.navOptions?.let {
            navContainer?.navigate(
                resId,
                args,
                navOptions { // Use the Kotlin DSL for building NavOptions
                    anim {
                        enter = R.anim.enter_from_right
                        exit = R.anim.exit_to_left
                        popEnter = R.anim.enter_from_left
                        popExit = R.anim.exit_to_right
                    }
                    popUpTo(it.popUpToId) {
                        inclusive = it.isPopUpToInclusive()
                    }
                }
            )
        }
    }

    fun pushFadeTo(@IdRes resId: Int, args: Bundle? = null) {
        navContainer?.currentDestination?.getAction(resId)?.navOptions?.let {
            binding.root.findNavController().navigate(
                resId,
                args,
                navOptions { // Use the Kotlin DSL for building NavOptions
                    anim {
                    enter = R.anim.fade_in
                    exit = R.anim.fade_out
                    }
                    popUpTo(it.popUpToId) {
                        inclusive = it.isPopUpToInclusive()
                    }
                }
            )
        }
    }

    fun popTo(@IdRes destinationId: Int?, inclusive: Boolean = false) {
        navContainer?.apply {
            if (destinationId == null) popBackStack()
            else popBackStack(destinationId, inclusive)
        }
    }

    fun popToRoot() {
        rootDes?.let { popTo(it, false) }
    }
}

abstract class BaseVMActivity<VM : BaseViewModel, B : ViewBinding> : BaseActivity<B>() {
    abstract val viewModel: VM

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        viewModel.isShowProgress.observe(this) { isShow ->
            showProgress(isShow)
        }
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            if (progress == null) {
                progress = ProgressView()
            }
            if (progress?.isVisible == true) {
                return
            }
            progress?.show(supportFragmentManager, "")
        } else {
            progress?.dismiss()
            progress = null
        }
    }
}
