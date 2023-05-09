package com.example.baseandroid.application.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.databinding.ItemLoadmoreBinding
import com.example.baseandroid.resource.utils.gone
import com.example.baseandroid.resource.utils.visibleIf

class LoadingAdapter : LoadStateAdapter<BaseViewHolder.LoadingViewHolder>() {

    override fun onBindViewHolder(holder: BaseViewHolder.LoadingViewHolder, loadState: LoadState) {
        holder.binding.progressBar.visibleIf(loadState == LoadState.Loading)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BaseViewHolder.LoadingViewHolder {
        val binding =
            ItemLoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder.LoadingViewHolder(binding)
    }



}
