package com.example.baseandroid.resource.customView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.baseandroid.application.base.BaseDialogFragment
import com.example.baseandroid.databinding.CustomDialogFragmentBinding
import javax.inject.Inject

class ProgressView @Inject constructor() :
    BaseDialogFragment<CustomDialogFragmentBinding>(CustomDialogFragmentBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }
}