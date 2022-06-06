package com.example.base.utils

import android.view.View
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.Qualifier
import java.text.SimpleDateFormat
import java.util.*

private val sdfWithSeconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    .apply { timeZone = TimeZone.getTimeZone("UTC") }

private val sdfWithoutSeconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
    .apply { timeZone = TimeZone.getTimeZone("UTC") }

inline fun <reified T : Any> getKoinInstance(qualifier: Qualifier? = null): Lazy<T> {
    return lazy {
        return@lazy object : KoinComponent {
            val value by inject<T>(qualifier)
        }.value
    }
}

val <T> T.exhaustive: T get() = this

fun List<ApplicationMessage.MessageAction>.getActions()
        : Triple<ApplicationMessage.MessageAction?, ApplicationMessage.MessageAction?,
        ApplicationMessage.MessageAction?> {
    val positiveAction = firstOrNull { it is ApplicationMessage.MessageAction.Positive }
    val negativeAction = firstOrNull { it is ApplicationMessage.MessageAction.Negative }
    val neutralAction = firstOrNull { it is ApplicationMessage.MessageAction.Neutral }
    return Triple(positiveAction, negativeAction, neutralAction)
}

fun Date?.toStringFormat(): String? {
    return when {
        this != null -> sdfWithSeconds.format(this)
        else -> null
    }
}

fun String?.toDate(): Date? {
    return when {
        !this.isNullOrEmpty() -> try {
            sdfWithSeconds.parse(this)
        } catch (e: Exception) {
            sdfWithoutSeconds.parse(this)
        }
        else -> Date()
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}