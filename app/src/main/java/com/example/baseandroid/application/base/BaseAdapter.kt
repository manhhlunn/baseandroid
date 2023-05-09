package com.example.baseandroid.application.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.baseandroid.databinding.ItemLoadmoreBinding
import com.example.baseandroid.resource.utils.safeLet

abstract class BaseAdapter<A : Any, B : ViewBinding>(callback: DiffUtil.ItemCallback<A>) :
    ListAdapter<A, BaseViewHolder<B>>(callback) {


    override fun submitList(list: List<A>?) {
        if (isShowLoading) removeLoading()
        super.submitList(list)
    }

    abstract fun createItemViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder.ItemViewHolder<B>

    abstract fun onBindItemViewHolder(
        holder: BaseViewHolder.ItemViewHolder<B>,
        item: A,
        position: Int
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        return if (viewType == LOADING) BaseViewHolder.LoadingViewHolder(
            ItemLoadmoreBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
        else createItemViewHolder(parent, viewType)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        safeLet(holder.getBaseViewHolder(), currentList.getOrNull(position)) { itemHolder, item ->
            onBindItemViewHolder(itemHolder, item, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] == null) LOADING else ITEM
    }

    private var isShowLoading = false

    companion object {
        const val LOADING = -1
        const val ITEM = 1
    }

    fun showLoading() {
        if (isShowLoading) return
        isShowLoading = true
        currentList.add(null)
        submitList(currentList)
    }

    private fun removeLoading() {
        if (isShowLoading) {
            currentList.removeLast()
            isShowLoading = false
            submitList(currentList)
        }
    }
}



sealed class BaseViewHolder<out B : ViewBinding>(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    class LoadingViewHolder(val binding: ItemLoadmoreBinding) : BaseViewHolder<Nothing>(binding)

    class ItemViewHolder<out B : ViewBinding>(val binding: B) :
        BaseViewHolder<B>(binding)

    fun getBaseViewHolder(): ItemViewHolder<B>? {
        return when (this) {
            is ItemViewHolder -> this
            is LoadingViewHolder -> null
        }
    }
}
