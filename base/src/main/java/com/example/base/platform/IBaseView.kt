package com.example.base.platform

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.example.base.utils.ApplicationMessage
import com.example.base.utils.ErrorType
import com.example.base.utils.Loading
import com.example.base.utils.LoadingStatus
import com.example.base.utils.MessageUtils.showErrorDialog
import com.example.base.utils.MessageUtils.showMessage
import com.example.base.utils.MessageUtils.showNoInternetErrorWithRetryDialog
import com.example.base.utils.Result
import com.example.base.utils.exhaustive
import com.example.base.utils.getActions
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

interface IBaseView<ViewModel : BaseViewModel> {
    val viewModel: ViewModel

    val sharedViewModels: MutableList<WeakReference<BaseViewModel>>

    val loadingHandler: LoadingHandler


    fun getCurrentActivity(): Activity

    fun getCurrentViewLifecycleOwner(): LifecycleOwner

    fun getViewModelStoreOwner(): ViewModelStoreOwner = this as ViewModelStoreOwner

    fun getSavedStateRegistryOwner(): SavedStateRegistryOwner? =
        if (this is SavedStateRegistryOwner) this else null

    @Suppress("UNCHECKED_CAST")
    fun viewModelClass(): KClass<ViewModel> {
        // dirty hack to get generic type https://stackoverflow.com/a/1901275/719212
        return ((javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<ViewModel>).kotlin
    }

    fun initGenericObservers() {
        initLoading(viewModel)
        initError(viewModel)
        initMessage(viewModel)
        initNavigation(viewModel)
        sharedViewModels
            .asSequence()
            .filter { it.get() != null }
            .map { it.get()!! }
            .forEach {
                initLoading(it)
                initError(it)
                initMessage(viewModel)
                initNavigation(it)
            }
    }

    fun initLoading(vm: BaseViewModel) {
        vm.loading.observe(getCurrentViewLifecycleOwner(), {
            when (it.status) {
                is LoadingStatus.Show -> showLoading(it)
                is LoadingStatus.Dismiss -> hideLoading(it)
            }.exhaustive
        })
    }

    fun initError(vm: BaseViewModel) {
        vm.error.observe(getCurrentViewLifecycleOwner(), {
            hideLoading(Loading(LoadingStatus.Dismiss.All))
            showError(it)
        })
    }

    fun initMessage(vm: BaseViewModel) {
        vm.message.observe(getCurrentViewLifecycleOwner(), { message ->
            showMessage(message)
        })
    }

    fun showMessage(message: ApplicationMessage.Message) {
        val viewType = message.viewType
        val messageType = message.messageType
        val actions = message.actions
        when (messageType) {
            is ApplicationMessage.MessageType.Success -> {
                when (viewType) {
                    is ApplicationMessage.ViewType.Dialog -> TODO()
                    is ApplicationMessage.ViewType.TopAlert -> {
                        val showMessage = getCurrentActivity().showMessage(
                            message = messageType.getMessageString(
                                getCurrentActivity()
                            )
                        )
                        showMessage
                    }
                    is ApplicationMessage.ViewType.Toast -> TODO()
                }.exhaustive
            }
            is ApplicationMessage.MessageType.Info, // todo handle info status
            is ApplicationMessage.MessageType.Error,
            -> {
                when (viewType) {
                    is ApplicationMessage.ViewType.Dialog -> {
                        val (
                            positiveAction,
                            negativeAction,
                            neutralAction,
                        ) = actions.getActions()

                        getCurrentActivity().showErrorDialog(
                            message = messageType.getMessageString(getCurrentActivity()),
                            title = messageType.getTitleString(getCurrentActivity()),
                            positiveButtonText = positiveAction?.getActionString(getCurrentActivity()),
                            positiveButtonAction = positiveAction?.method,
                            negativeButtonText = negativeAction?.getActionString(getCurrentActivity()),
                            negativeButtonAction = negativeAction?.method,
                            neutralButtonText = neutralAction?.getActionString(getCurrentActivity()),
                            neutralButtonAction = negativeAction?.method,
                            cancelable = viewType.cancelable
                        )
                    }
                    is ApplicationMessage.ViewType.TopAlert -> TODO()
                    is ApplicationMessage.ViewType.Toast -> TODO()
                }.exhaustive
            }
        }.exhaustive
    }

    fun initNavigation(vm: BaseViewModel) {
        vm.nextScreen.observe(getCurrentViewLifecycleOwner(), {
            handleNavigation(it)
        })
    }

    fun showError(error: Result.Error) {
        when (error.exception.type) {
            ErrorType.Network.NoInternetConnection -> showNoInternetErrorWithRetryDialog(error)
            ErrorType.Network.NotFound,
            ErrorType.Network.BadRequest,
            ErrorType.Network.Unauthorized,
            ErrorType.Network.Unexpected,
            ErrorType.Network.Forbidden,
            ErrorType.Network.MethodNotAllowed,
            ErrorType.Network.NotAcceptable,
            ErrorType.Network.PreconditionFailed,
            ErrorType.Network.InternalServerError,
            ErrorType.Network.UnsupportedMediaType,
            ErrorType.Local.ContentProvider.ReadContactError,
            ErrorType.Unexpected,
            ErrorType.UserError,
            ErrorType.Firebase,
            ErrorType.NotError,
            is ErrorType.Validation,
            is ErrorType.BusinessValidation,
            -> showErrorDialog(error)
            else -> TODO()
        }.exhaustive
    }

    fun hideLoading(loading: Loading) {
        // todo handle multiple cases of hide loading (full, list, etc...)
        loadingHandler.hideLoading()
    }

    fun showLoading(loading: Loading) {
        loadingHandler.showLoading()
    }

    fun handleNavigation(it: BaseNavigationDestination<*, *>) = when (it) {
        is BaseNavigationDestination.Activities -> it.start(getCurrentActivity())
        is BaseNavigationDestination.Fragments ->
            if (this is Fragment) it.start(this)
            else {
                /*pass*/
            }
        is BaseNavigationDestination.ActivitiesFromContext -> it.start(getCurrentActivity())
    }.exhaustive
}