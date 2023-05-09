package com.example.baseandroid.application.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.databinding.ItemLoadmoreBinding

interface ValueBaseAdapter<B : ViewBinding> {
    @IntRange(from = 0)
    fun getViewType(): Int
    fun createViewBinding(parent: ViewGroup): B
}

abstract class BaseMultipleAdapter(callback: DiffUtil.ItemCallback<ValueBaseAdapter<*>>) :
    BaseAdapter<ValueBaseAdapter<*>, ViewBinding>(callback) {

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] == null) LOADING else currentList[position].getViewType()
    }

    override fun createItemViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder.ItemViewHolder<ViewBinding> {
        val value = currentList.find { viewType == it?.getViewType() }
        val binding = value?.createViewBinding(parent) ?: ItemLoadmoreBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return BaseViewHolder.ItemViewHolder(binding)
    }
}



