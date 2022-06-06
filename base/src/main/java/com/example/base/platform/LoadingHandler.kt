package com.example.base.platform

import android.app.Activity
import android.app.Dialog
import java.lang.ref.WeakReference

class LoadingHandler private constructor(val activity: WeakReference<Activity>) {
    private lateinit var progressDialog: WeakReference<Dialog>

    init {
        if (!::progressDialog.isInitialized) {
            createDialog()
        }
    }

    private fun createDialog() {
        //not created for simplicity
    }

    fun showLoading() {
        if (::progressDialog.isInitialized) {
            progressDialog.get() ?: run { createDialog() }
            progressDialog.get()?.run { if (!isShowing) show() }
        }
    }

    fun hideLoading() {
        if (::progressDialog.isInitialized) progressDialog.get()?.run { dismiss() }
    }

    fun stop() {
        if (::progressDialog.isInitialized) progressDialog.get()?.run { if (isShowing) cancel() }
    }

    companion object {
        @JvmStatic
        fun getInstance(activity: Activity): LoadingHandler {
            return LoadingHandler(WeakReference(activity))
        }
    }
}