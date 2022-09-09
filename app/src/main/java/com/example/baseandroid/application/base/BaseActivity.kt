package com.example.baseandroid.application.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.resource.customView.ProgressView

abstract class BaseActivity<V : BaseViewModel, B : ViewBinding> : AppCompatActivity() {

    val activityScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    abstract val viewModel: V

    var progress: ProgressView? = null

    lateinit var binding: B

    var tabbar: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeViewBinding()
        setContentView(binding.root)
        setupView(savedInstanceState)
        viewModel.isShowProgress.observe(this) { isShow ->
            showProgress(isShow)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancelCoroutines()
    }
}
