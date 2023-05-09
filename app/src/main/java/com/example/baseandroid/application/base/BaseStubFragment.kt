package com.example.baseandroid.application.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.databinding.FragmentStubBinding

abstract class BaseViewStubFragment<B : ViewBinding>(private val stubInflate: (View) -> B) :
    BaseFragment<FragmentStubBinding>(FragmentStubBinding::inflate) {

    private var hasInflated = false
    private var visible = false
    private var savedInstanceState: Bundle? = null
    var bindingOrigin: B by autoCleaned()

    protected abstract fun onCreateViewAfterViewStubInflated(
        inflatedView: View,
        savedInstanceState: Bundle?
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.stub.layoutResource = getViewStubLayoutResource()
        binding.stub.setOnInflateListener { _, inflated -> bindingOrigin = stubInflate(inflated) }
        this.savedInstanceState = savedInstanceState
        if (visible && !hasInflated) {
            val inflatedView = binding.stub.inflate()
            onCreateViewAfterViewStubInflated(inflatedView, savedInstanceState)
            afterViewStubInflated(view)
        }

        return view
    }

    @LayoutRes
    protected abstract fun getViewStubLayoutResource(): Int

    @CallSuper
    protected fun afterViewStubInflated(originalViewContainerWithViewStub: View?) {
        hasInflated = true
    }

    override fun onResume() {
        super.onResume()
        visible = true
        if (!hasInflated) {
            if (binding.stub.parent != null) {
                val inflatedView = binding.stub.inflate()
                onCreateViewAfterViewStubInflated(inflatedView, savedInstanceState)
            }
            afterViewStubInflated(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasInflated = false
    }

    override fun onPause() {
        super.onPause()
        visible = false
    }

    override fun onDetach() {
        super.onDetach()
        hasInflated = false
    }
}
