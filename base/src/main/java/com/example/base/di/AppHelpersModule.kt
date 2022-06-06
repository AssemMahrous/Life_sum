package com.example.base.di

import com.example.base.common.ApplicationContext
import com.example.base.common.ApplicationResource
import com.example.base.common.IApplicationContext
import com.example.base.common.IApplicationResource
import com.example.base.utils.ConnectivityUtils
import com.example.base.utils.IConnectivityUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

object AppHelpersModule {
    lateinit var module: Module
        private set

    fun init() {
        this.module = module {
            // resource helper
            single<IApplicationResource> { ApplicationResource() }

            // resource helper
            single<IApplicationContext> { ApplicationContext(androidContext()) }

            // checking connectivity helper
            single<IConnectivityUtils> { ConnectivityUtils(androidContext()) }

        }
    }
}