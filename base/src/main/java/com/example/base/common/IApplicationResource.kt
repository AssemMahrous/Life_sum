package com.example.base.common

import androidx.annotation.StringRes

interface IApplicationResource {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
    fun getStringEnglishNumbers(@StringRes id: Int, vararg formatArgs: Any): String
}