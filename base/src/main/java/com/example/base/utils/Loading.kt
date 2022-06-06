package com.example.base.utils

data class Loading(val status: LoadingStatus)

sealed class LoadingStatus {
    sealed class Show : LoadingStatus() {
        object FullScreenNotCancelable : Show()
        object FullScreenCancelable : Show()
        object Pagination : Show()
    }

    sealed class Dismiss : LoadingStatus() {
        object Pagination : Dismiss()
        object FullScreen : Dismiss()
        object All : Dismiss()
    }
}