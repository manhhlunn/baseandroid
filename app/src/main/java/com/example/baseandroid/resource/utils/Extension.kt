package com.example.baseandroid.resource.utils

import android.animation.Animator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.baseandroid.R
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.application.base.MyActivity
import com.example.baseandroid.application.base.MyFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun MyFragment.delay(
    timeMillis: Long, execute: () -> Unit
) {
    fragmentScope.launch {
        kotlinx.coroutines.delay(timeMillis)
        execute()
    }
}

fun MyActivity.delay(
    timeMillis: Long, execute: () -> Unit
) {
    activityScope.launch {
        kotlinx.coroutines.delay(timeMillis)
        execute()
    }
}

fun View.stopAnimation() {
//    this.animation.cancel()
}

fun View.visibleIf(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}

fun View.visible() {
    this.visibility = View.VISIBLE
    this.isEnabled = true
}

fun View.hidden() {
    this.visibility = View.INVISIBLE
    this.isEnabled = false
}

fun View.gone() {
    this.visibility = View.GONE
    this.isEnabled = false
}


fun View.animeFade(isShow: Boolean, duration: Long = 0) {
    if (isShow == isVisible) {
        return
    }
    val toAlpha = if (isShow) 1f else 0f
    this.visible()
    this.alpha = if (isShow) 0f else 1f
    animate()
        .alpha(toAlpha)
        .setDuration(duration)
        .setComplete { if (isShow) visible() else gone() }
        .start()
}

fun Context.showSingleActionAlert(
    title: String, message: String,
    actionTitle: String = "OK",
    completion: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(actionTitle) { _, _ ->
            completion()
        }
        .setCancelable(false)
        .create()
        .apply {
            setCanceledOnTouchOutside(false)
            show()
        }
}

fun Context.showTwoActionAlert(
    title: String, message: String,
    positiveTitle: String = "OK",
    negativeTitle: String = "Cancel",
    positiveAction: (() -> Unit)? = null,
    negativeAction: (() -> Unit)? = null
) {
    CoroutineScope(Dispatchers.Main).launch {
        AlertDialog.Builder(this@showTwoActionAlert)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveTitle) { _, _ ->
                positiveAction?.let { it() }
            }
            .setNegativeButton(negativeTitle) { _, _ ->
                negativeAction?.let { it() }
            }
            .setCancelable(false)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }
}

suspend fun Context.showTwoActionAlert(
    title: String, message: String,
    positiveTitle: String = "OK",
    negativeTitle: String = "Cancel"
) = suspendCoroutine<Boolean> { continuation ->
    showTwoActionAlert(title, message, positiveTitle, negativeTitle, positiveAction = {
        continuation.resume(true)
    })
}

fun View.jumping(translationY: Float = 20F, duration: Long, loop: Boolean = true) {
    animate()
        .translationY(translationY)
        .setDuration(duration / 2)
        .setComplete {
            animate()
                .translationY(-translationY)
                .setDuration(1500L)
                .setComplete {
                    if (loop) {
                        jumping(translationY, duration, loop)
                    }
                }
        }
}

fun View.toggleSelected() {
    isSelected = !isSelected
}

fun View.toggleVisible() {
    if (isVisible) {
        gone()
    } else {
        visible()
    }
}

fun View.animeRotate(rotation: Float) {
    animate()
        .rotation(rotation)
        .setDuration(200)
        .start()
}

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.isInternetAvailable(): Boolean {
    val result: Boolean
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.string(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}


fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun MyFragment.checkPermission(
    perms: Array<String>,
) {
    if (requireContext().hasPermissions(perms)) {
        onPermissionGranted(perms.associateWith { true })
    } else {
        permissionsResult.launch(perms)
    }
}

fun ViewPropertyAnimator.setComplete(completion: (Animator?) -> Unit): ViewPropertyAnimator {
    return setListener(object : Animator.AnimatorListener {

        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            completion(p0)
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }
    })
}

fun View.setPaddingAsDP(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(asPixels(left), asPixels(top), asPixels(right), asPixels(bottom))
}

fun View.asPixels(value: Int): Int {
    val scale = resources.displayMetrics.density
    val dpAsPixels = (value * scale + 0.5f)
    return dpAsPixels.toInt()
}

sealed class ResultResponse<out R> {
    data class Success<R>(val value: R) : ResultResponse<R>()
    data class Error(val exception: Exception) :
        ResultResponse<Nothing>()

    data class DefaultError(val httpException: HttpException) :
        ResultResponse<Nothing>()
}

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?,
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun <R> BaseViewModel.request(
    flow: Flow<ResultResponse<R>>,
    isShowActivityLoading: Boolean = true,
    error: MutableLiveData<Exception>? = null,
    success: (R) -> Unit
) {
    viewModelScope.launch {
        if (isShowActivityLoading) showProgress()
        flow.collect {
            if (isShowActivityLoading) hideProgress()
            when (it) {
                is ResultResponse.Error -> error?.postValue(it.exception)
                is ResultResponse.DefaultError -> onHandleException(it.httpException)
                is ResultResponse.Success -> success.invoke(it.value)
            }
        }
    }
}

fun AppCompatImageView.setImageUrl(
    url: String?,
    radius: Int = 0,
    isCenterCrop: Boolean = true,
    errDrawable: Int = R.drawable.ic_launcher_background,
) {
    url?.let {
        val multiTransformation: MultiTransformation<Bitmap> = if (radius != 0) {
            MultiTransformation(
                if (isCenterCrop) CenterCrop() else FitCenter(),
                RoundedCorners(radius.dp)
            )
        } else {
            MultiTransformation(
                if (isCenterCrop) CenterCrop() else FitCenter()
            )
        }

        Glide.with(context)
            .load(url)
            .apply(RequestOptions.bitmapTransform(multiTransformation))
            .error(errDrawable)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }


            })
            .placeholder(createPlaceholder(context))
            .into(this)
    }
}

fun createPlaceholder(context: Context) = CircularProgressDrawable(context).also {
    it.strokeWidth = 5f
    it.centerRadius = 30f
    it.start()
}

fun View.hideKeyboard(context: Context?) {
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

fun Activity.setupActivityFullScreen(
    view: View,
    isHideStatusBar: Boolean = true,
    isHideNavigationBar: Boolean = true
) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    if (isHideNavigationBar) window.navigationBarColor =
        ContextCompat.getColor(this, android.R.color.transparent)
    if (isHideStatusBar) window.statusBarColor =
        ContextCompat.getColor(this, android.R.color.transparent)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    ViewCompat.setOnApplyWindowInsetsListener(view) { root, windowInset ->
        val inset = windowInset.getInsets(WindowInsetsCompat.Type.systemBars())
        root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = inset.left
            bottomMargin = if (isHideNavigationBar) 0 else inset.bottom
            rightMargin = inset.right
            topMargin = if (isHideStatusBar) 0 else inset.top
        }
        WindowInsetsCompat.CONSUMED
    }
}