package com.example.base.utils

import android.app.Activity
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.example.base.R
import com.example.base.platform.IBaseView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.Alerter

object MessageUtils {
    @JvmStatic
    fun Activity?.showMessage(
        message: String,
        @Suppress("UNUSED_PARAMETER") title: String? = null,
        @ColorRes backgroundColorRes: Int? = R.color.purple_200,
        @StyleRes textAppearance: Int? = null,
    ): Alert? {
        return this?.let {
            val alerter = Alerter.create(it)
                .setText(message)
            backgroundColorRes?.let { alerter.setBackgroundColorRes(it) }
            textAppearance?.let { alerter.setTextAppearance(it) }
            alerter.hideIcon()
            alerter.setContentGravity(Gravity.CENTER)
            alerter.show()
        }
    }

    @JvmStatic
    fun Fragment.showMessage(
        title: String,
        message: String,
        @ColorRes backgroundColorRes: Int?,
        @StyleRes textAppearance: Int?,
    ): Alert? {
        return activity.showMessage(title, message, backgroundColorRes, textAppearance)
    }

    @JvmStatic
    fun Activity?.showMessageWithCustomView(
        @LayoutRes customLayoutResource: Int,
        @ColorRes backgroundColorRes: Int?,
        onInflate: ((View?) -> Unit)? = null,
    ): Alert? {
        return this?.let {
            val alerter = Alerter.create(it, customLayoutResource)
            backgroundColorRes?.let { alerter.setBackgroundColorRes(it) }
            alerter.also {
                onInflate?.invoke(it.getLayoutContainer())
            }
            alerter.show()
        }
    }

    @JvmStatic
    fun Fragment.showMessageWithCustomView(
        @LayoutRes customLayoutResource: Int,
        @ColorRes backgroundColorRes: Int?,
        onInflate: ((View?) -> Unit)? = null,
    ): Alert? {
        return activity
            .showMessageWithCustomView(
                customLayoutResource,
                backgroundColorRes,
                onInflate
            )
    }

    @JvmStatic
    fun IBaseView<*>.showNoInternetErrorWithRetryDialog(
        error: Result.Error,
        cancelable: Boolean = true,
        onPositiveButtonClick: (() -> Unit)? = null,
    ) {
        val errorMessage = error.exception.getErrorMessageString()
        getCurrentActivity().showErrorDialog(
            message = errorMessage,
            positiveButtonAction = {
                viewModel.clearRetry()
                onPositiveButtonClick?.invoke()
            },
            negativeButtonText = getCurrentActivity().getString(R.string.action_retry),
            negativeButtonAction = { viewModel.retry() },
            onCancel = { viewModel.clearRetry() },
            cancelable = cancelable
        )
    }

    @JvmStatic
    fun Fragment.showNoInternetErrorWithRetryDialog(
        error: Result.Error,
        buttonActionRetry: (() -> Unit)?,
        buttonActionCancel: (() -> Unit)?,
    ) {
        val errorMessage = error.exception.getErrorMessageString()
        activity?.showErrorDialog(
            message = errorMessage,
            positiveButtonAction = buttonActionCancel,
            negativeButtonText = getString(R.string.action_retry),
            negativeButtonAction = buttonActionRetry
        )
    }

    @JvmStatic
    fun Activity.showNoInternetErrorWithRetryDialog(
        error: Result.Error,
        buttonActionRetry: (() -> Unit)?,
        buttonActionCancel: (() -> Unit)?,
    ) {
        val errorMessage = error.exception.getErrorMessageString()
        showErrorDialog(
            message = errorMessage,
            positiveButtonAction = buttonActionCancel,
            negativeButtonText = getString(R.string.action_retry),
            negativeButtonAction = buttonActionRetry
        )
    }

    @JvmStatic
    fun IBaseView<*>.showErrorDialog(error: Result.Error) {
        getCurrentActivity()
            .showErrorDialog(
                message = error.exception
                    .getErrorMessageString()
            )
    }

    @JvmStatic
    fun Activity.showErrorDialog(
        message: String,
        title: String? = null,
        positiveButtonText: String? = null,
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        neutralButtonAction: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        cancelable: Boolean = true,
    ) {
        showDialog(
            message = message,
            title = title ?: getString(R.string.title_error),
            positiveButtonText = positiveButtonText ?: getString(R.string.action_ok),
            positiveButtonAction = positiveButtonAction,
            negativeButtonText = negativeButtonText,
            negativeButtonAction = negativeButtonAction,
            neutralButtonText = neutralButtonText,
            neutralButtonAction = neutralButtonAction,
            onCancel = onCancel,
            onDismiss = onDismiss,
            cancelable = cancelable
        )
    }

    @JvmStatic
    fun Activity.showDialog(
        message: String,
        title: String? = null,
        positiveButtonText: String = getString(R.string.action_ok),
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        neutralButtonAction: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        cancelable: Boolean = true,
        @StyleRes themeId: Int = R.style.AlertDialogTheme,
    ) {
        MaterialAlertDialogBuilder(this, themeId).apply {
            title?.let { setTitle(title) }
            setMessage(message)
            setCancelable(cancelable)
            setPositiveButton(positiveButtonText) { _, _ -> positiveButtonAction?.invoke() }
            negativeButtonText?.let { setNegativeButton(it) { _, _ -> negativeButtonAction?.invoke() } }
            neutralButtonText?.let { setNeutralButton(it) { _, _ -> neutralButtonAction?.invoke() } }
            onCancel?.let { action -> setOnCancelListener { action() } }
            onDismiss?.let { action -> setOnDismissListener { action() } }
        }.show()
    }
}