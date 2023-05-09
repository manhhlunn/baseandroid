package com.example.baseandroid.application.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias MyFragment = BaseFragment<*>
typealias MyActivity = BaseActivity<*>
typealias InflateFM<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<B : ViewBinding>(private val inflate: InflateFM<B>) : Fragment() {

    val fragmentScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    val permissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                onPermissionGranted(permissions)
            } else {
                onPermissionDenied(permissions)
            }
        }


    var binding: B by autoCleaned()
    open fun setupView(savedInstanceState: Bundle?) {}
    open fun setupObserve(savedInstanceState: Bundle?) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(savedInstanceState)
        setupObserve(savedInstanceState)
    }

    open fun onPermissionGranted(permissions: Map<String, @JvmSuppressWildcards Boolean>) {}
    open fun onPermissionDenied(permissions: Map<String, @JvmSuppressWildcards Boolean>) {}

    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancelCoroutines()
    }
}

abstract class BaseVMFragment<B : ViewBinding, V : BaseViewModel>(inflate: InflateFM<B>) :
    BaseFragment<B>(inflate) {
    abstract val viewModel: V
    private val activityVM: BaseViewModel? by lazy {
        (activity as? BaseVMActivity<*, *>)?.viewModel
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        viewModel.isShowProgress.observe(viewLifecycleOwner) { isShow ->
            activityVM?.apply {
                if (isShow) showProgress()
                else hideProgress()
            }
        }
        viewModel.handleException.observe(viewLifecycleOwner) {
            activityVM?.onHandleException(it)
        }
    }
}

interface NavigationFragment {
    fun initNavigation(fragment: BaseVMFragment<*, *>, navigationAction: NavigationAction)
}

class NavigationFragmentImpl : NavigationFragment {
    override fun initNavigation(
        fragment: BaseVMFragment<*, *>,
        navigationAction: NavigationAction
    ) {
        navigationAction.navControllerControl.observe(fragment.viewLifecycleOwner) { nav ->
            nav(fragment.findNavController())
        }
    }

}

class AutoCleanedValue<T>(
    fragment: Fragment,
    private val initializer: (() -> T)?
) : ReadWriteProperty<Fragment, T> {

    private var _value: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerObserver = Observer<LifecycleOwner?> { viewLifecycleOwner ->
                viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {

                    override fun onDestroy(owner: LifecycleOwner) {
                        _value = null
                    }
                })
            }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerObserver)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerObserver)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val value = _value

        if (value != null) {
            return value
        }

        if (thisRef.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            return initializer?.invoke().also { _value = it }
                ?: throw IllegalStateException("The value has not yet been set or no default initializer provided")
        } else {
            throw IllegalStateException("Fragment might have been destroyed or not initialized yet")
        }
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        _value = value
    }
}

fun <T : Any> Fragment.autoCleaned(initializer: (() -> T)? = null): AutoCleanedValue<T> {
    return AutoCleanedValue(this, initializer)
}



