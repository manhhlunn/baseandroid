package com.example.baseandroid.resource.utils

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupLoadMore(minItem: Int = 20, completion: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if ((adapter?.itemCount ?: 0) < minItem) {
                return
            }
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
            if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == (adapter?.itemCount
                    ?: 2) - 1
            ) {
                completion()
            }
        }
    })
}

fun RecyclerView.setDivider(
    @DrawableRes drawableRes: Int,
    orientation: Int = DividerItemDecoration.VERTICAL
) {
    val divider = DividerItemDecoration(
        this.context,
        orientation
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}
