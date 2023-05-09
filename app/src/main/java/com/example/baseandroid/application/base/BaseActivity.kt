package com.example.baseandroid.application.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.AnimBuilder
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.R
import com.example.baseandroid.resource.customView.ProgressView
import com.example.baseandroid.resource.utils.SingleLiveEvent


abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {

    val activityScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        setupView(savedInstanceState)
        setupObserve(savedInstanceState)
    }

    open fun setupView(savedInstanceState: Bundle?) {}
    open fun setupObserve(savedInstanceState: Bundle?) {}
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancelCoroutines()
    }
}


abstract class BaseVMActivity<B : ViewBinding, VM : BaseViewModel>(inflate: (LayoutInflater) -> B) :
    BaseActivity<B>(inflate) {
    abstract val viewModel: VM
    private var progress: ProgressView? = null

    override fun setupObserve(savedInstanceState: Bundle?) {
        super.setupObserve(savedInstanceState)
        viewModel.isShowProgress.observe(this) { isShow ->
            showProgress(isShow)
        }
        viewModel.handleException.observe(this) {

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

interface ClearFocus {
    fun onEventClearFocus(
        activity: Activity,
        event: MotionEvent,
        dispatchTouchEvent: Boolean
    ): Boolean
}

class ClearFocusImpl : ClearFocus {
    override fun onEventClearFocus(
        activity: Activity,
        event: MotionEvent,
        dispatchTouchEvent: Boolean
    ): Boolean {
        val view = activity.currentFocus
        if (view is EditText) {
            val w = activity.currentFocus ?: return dispatchTouchEvent
            val arr = IntArray(2)
            w.getLocationOnScreen(arr)
            val x = event.rawX + w.left - arr[0]
            val y = event.rawY + w.top - arr[1]
            if (event.action == MotionEvent.ACTION_UP && (x < w.left || x >= w.right || y < w.top || y > w.bottom)) {
                val imm =
                    activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.window.currentFocus?.windowToken, 0)
                view.clearFocus()
            }
        }
        return dispatchTouchEvent
    }
}

enum class PushType(val anim: AnimBuilder) {
    NONE(AnimBuilder().apply {}),
    SLIDE(
        AnimBuilder().apply {
            enter = R.anim.enter_from_right
            exit = R.anim.exit_to_left
            popEnter = R.anim.enter_from_left
            popExit = R.anim.exit_to_right
        }
    ),
    FADE(AnimBuilder().apply {
        enter = R.anim.fade_in
        exit = R.anim.fade_out
    })
}

interface NavigationActivity {
    fun initNavigation(activity: BaseVMActivity<*, *>, navigationAction: NavigationAction)
}

class NavigationActivityImpl(
    @IdRes val navHostId: Int
) : NavigationActivity {

    override fun initNavigation(
        activity: BaseVMActivity<*, *>,
        navigationAction: NavigationAction
    ) {
        val navHostFragment =
            activity.supportFragmentManager.findFragmentById(navHostId) as NavHostFragment
        val navController = navHostFragment.navController

        navigationAction.navControllerControl.observe(activity) {
            it(navController)
        }
    }

}

class NavigationActionImpl : NavigationAction {
    override val navControllerControl: LiveData<NavController.() -> Unit> get() = _navControllerControl
    private val _navControllerControl = SingleLiveEvent<NavController.() -> Unit>()

    private fun getNavOptions(
        optionalPopUpToId: Int? = null,
        inclusive: Boolean? = null,
        pushType: PushType = PushType.NONE
    ) = NavOptions.Builder().apply {
        setEnterAnim(pushType.anim.enter)
        setExitAnim(pushType.anim.exit)
        setPopEnterAnim(pushType.anim.popEnter)
        setPopExitAnim(pushType.anim.popExit)
        optionalPopUpToId?.let {
            setPopUpTo(optionalPopUpToId, inclusive ?: false)
        }
    }.build()

    override fun goBackUpTo(
        destinyId: Int,
        inclusive: Boolean
    ) = _navControllerControl.postValue {
        navigate(
            destinyId, null, getNavOptions(
                optionalPopUpToId = destinyId,
                inclusive = inclusive
            )
        )
    }

    override fun navigateInDirection(
        directions: NavDirections,
        pushType: PushType
    ) {
        _navControllerControl.postValue {
            navigate(directions, getNavOptions(pushType = pushType))
        }
    }

    override fun navigateUp() {
        _navControllerControl.postValue {
            navigateUp()
        }
    }

    override fun popBackStack() {
        _navControllerControl.postValue {
            popBackStack()
        }
    }

}

interface NavigationAction {

    val navControllerControl: LiveData<NavController.() -> Unit>
    fun navigateInDirection(directions: NavDirections, pushType: PushType = PushType.FADE)
    fun goBackUpTo(destinyId: Int, inclusive: Boolean)
    fun navigateUp()
    fun popBackStack()
}




