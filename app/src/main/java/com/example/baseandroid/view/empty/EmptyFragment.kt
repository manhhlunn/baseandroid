package com.example.baseandroid.view.empty

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.baseandroid.application.base.BaseFragment
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.databinding.FragmentEmptyBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EmptyFragment : BaseFragment<EmptyFragmentViewModel, FragmentEmptyBinding>() {

    override val viewModel: EmptyFragmentViewModel by viewModels()

    override fun makeViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        super.makeViewBinding(inflater, container, savedInstanceState)
        binding = FragmentEmptyBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        super.setupView()
        binding.root.setBackgroundColor(Color.BLUE)
    }
}

@HiltViewModel
class EmptyFragmentViewModel @Inject constructor() : BaseViewModel() {

}