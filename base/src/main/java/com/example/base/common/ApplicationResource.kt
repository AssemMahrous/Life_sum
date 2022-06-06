package com.example.base.common

import android.content.Context
import androidx.annotation.StringRes

import com.example.base.utils.getKoinInstance
import java.util.*

class ApplicationResource : IApplicationResource {
    private val applicationContext by getKoinInstance<IApplicationContext>()
    private val context: Context
        get() = LocaleHelper.onAttach(applicationContext.context)

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    override fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }

    override fun getStringEnglishNumbers(id: Int, vararg formatArgs: Any): String {
        return getString(id).format(Locale.ENGLISH, *formatArgs)
    }
}