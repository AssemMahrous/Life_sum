package com.example.base.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.Qualifier


fun <T> MutableLiveData<T>.toLiveData(): LiveData<T> = this

inline fun <reified T : Any> getKoinInstance(qualifier: Qualifier? = null): Lazy<T> {
    return lazy {
        return@lazy object : KoinComponent {
            val value by inject<T>(qualifier)
        }.value
    }
}


inline fun <reified T> dependantLiveData(
    vararg dependencies: LiveData<out Any>,
    defaultValue: T? = null,
    crossinline mapper: () -> T?,
): MutableLiveData<T> =
    MediatorLiveData<T>().also { mediatorLiveData ->
        val observer = Observer<Any> { mediatorLiveData.value = mapper() }
        dependencies.forEach { dependencyLiveData ->
            mediatorLiveData.addSource(dependencyLiveData, observer)
        }
    }.apply { value = defaultValue }

inline fun <reified T, reified R> dependantLiveDataWithHandler(
    vararg dependencies: LiveData<out R>,
    defaultValue: T? = null,
    crossinline handler: (MediatorLiveData<T>, R) -> Unit,
): MutableLiveData<T> =
    MediatorLiveData<T>().also { mediatorLiveData ->
        val observer = Observer<R> { handler(mediatorLiveData, it) }
        dependencies.onEach {
            mediatorLiveData.addSource(it, observer)
        }
    }.apply { value = defaultValue }

val <T> T.exhaustive: T get() = this

fun List<ApplicationMessage.MessageAction>.getActions()
        : Triple<ApplicationMessage.MessageAction?, ApplicationMessage.MessageAction?,
        ApplicationMessage.MessageAction?> {
    val positiveAction = firstOrNull { it is ApplicationMessage.MessageAction.Positive }
    val negativeAction = firstOrNull { it is ApplicationMessage.MessageAction.Negative }
    val neutralAction = firstOrNull { it is ApplicationMessage.MessageAction.Neutral }
    return Triple(positiveAction, negativeAction, neutralAction)
}