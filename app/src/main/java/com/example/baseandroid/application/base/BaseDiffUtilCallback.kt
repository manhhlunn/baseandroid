package com.example.baseandroid.application.base

import androidx.recyclerview.widget.DiffUtil

class BaseDiffUtilCallback<A : Any> : DiffUtil.ItemCallback<A>() {

    override fun areItemsTheSame(oldItem: A, newItem: A) = oldItem == newItem

    override fun areContentsTheSame(oldItem: A, newItem: A) = true

}