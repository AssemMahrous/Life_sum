package com.example.base.platform

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.base.common.LocaleHelper
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import java.lang.ref.WeakReference

abstract class BaseActivity<ViewModel : BaseViewModel> : AppCompatActivity(), IBaseView<ViewModel> {

    override val viewModel: ViewModel by lazy {
        getKoin().getViewModel(
            owner = getViewModelStoreOwner(),
            clazz = viewModelClass()
        )
    }


    override val sharedViewModels = mutableListOf<WeakReference<BaseViewModel>>()

    override lateinit var loadingHandler: LoadingHandler

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.onAttach(this)
        loadingHandler = LoadingHandler.getInstance(this)
        initGenericObservers()
    }

    @CallSuper
    override fun onStop() {
        loadingHandler.stop()
        super.onStop()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        LocaleHelper.onAttach(this)
        super.onConfigurationChanged(newConfig)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase!!))
    }

    override fun getCurrentActivity(): Activity = this

    override fun getCurrentViewLifecycleOwner(): LifecycleOwner = this
}
