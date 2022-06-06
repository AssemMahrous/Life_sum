package com.example.base.di

import okhttp3.Interceptor
import org.koin.core.module.Module

object KoinMainModules {
    val moduleList = arrayListOf<Module>()

    fun initApplicationModule(
        baseUrl: String,
        interceptors: List<Interceptor>,
    ): ArrayList<Module> {

        // serialization module
        SerializationModule.init()
        moduleList.add(SerializationModule.module)

        // remote data source module
        NetworkModule.init(baseUrl, interceptors)
        moduleList.add(NetworkModule.module)

        // application helpers module
        AppHelpersModule.init()
        moduleList.add(AppHelpersModule.module)

        return moduleList
    }
}