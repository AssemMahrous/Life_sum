package com.dsquares.lucky.basemodule.base.platform

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import com.example.base.platform.BaseViewModel
import com.example.base.platform.IBaseView
import com.example.base.platform.LoadingHandler
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.lang.ref.WeakReference

abstract class BaseFragment<ViewModel : BaseViewModel> : Fragment(), IBaseView<ViewModel> {
    val navController: NavController by lazy { findNavController() }

    override val viewModel: ViewModel by lazy {
        getViewModel(
            clazz = viewModelClass(),
            owner = { ViewModelOwner.from(getViewModelStoreOwner()) })
    }

    override val sharedViewModels = mutableListOf<WeakReference<BaseViewModel>>()

    override lateinit var loadingHandler: LoadingHandler

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        loadingHandler = LoadingHandler.getInstance(requireActivity())
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v: View, insets: WindowInsetsCompat ->
            v.updateLayoutParams<MarginLayoutParams> {
                topMargin = 0
                bottomMargin = insets.systemWindowInsetBottom
            }
            insets.consumeSystemWindowInsets()
        }
        initGenericObservers()
        initializeViews()
        subscribeToViewModelUpdates()
    }


    @CallSuper
    override fun onStop() {
        loadingHandler.stop()
        super.onStop()
    }

    override fun getCurrentActivity(): Activity = requireActivity()

    override fun getCurrentViewLifecycleOwner(): LifecycleOwner = viewLifecycleOwner

    /**
     * You can extend this method to safely subscribe to your view model's observables.
     */
    protected open fun subscribeToViewModelUpdates() =
        Unit // No-op, just a subscription placeholder

    /**
     * You can extend this method to safely initialize your views.
     */
    protected open fun initializeViews() = Unit // No-op, just an initialization placeholder
}
