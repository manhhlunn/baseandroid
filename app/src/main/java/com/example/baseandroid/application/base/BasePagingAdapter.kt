package com.example.baseandroid.application.base

import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

abstract class BasePagingAdapter<A : Any, B : ViewBinding>(
    callback: DiffUtil.ItemCallback<A>
) :
    PagingDataAdapter<A, BaseViewHolder.ItemViewHolder<B>>(callback) {


    override fun onBindViewHolder(holder: BaseViewHolder.ItemViewHolder<B>, position: Int) {
        getItem(position)?.let { safeData ->
            bindViewHolderItem(holder, safeData, position)
        }
    }

    abstract fun bindViewHolderItem(
        holder: BaseViewHolder.ItemViewHolder<B>,
        item: A,
        position: Int
    )

}

