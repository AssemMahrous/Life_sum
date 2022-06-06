package com.example.lifesum.di

import com.example.lifesum.MainViewModel
import com.example.lifesum.common.remote.ApplicationRemoteDataSource
import com.example.lifesum.common.remote.IApplicationRemoteDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object FeaturesKoinModules {
    private lateinit var appNetworkModule: Module


    val allModules: ArrayList<Module>
        get() {
            val list = arrayListOf<Module>()

            // general
            list.add(appNetworkModule)

            list.add(module { viewModel<MainViewModel>() })

            return list
        }

    fun init() {
        // remote data source
        initAppNetworkModule()

    }

    private fun initAppNetworkModule() {
        appNetworkModule = module {
            single<IApplicationRemoteDataSource> { ApplicationRemoteDataSource(get()) }
        }
    }
}
