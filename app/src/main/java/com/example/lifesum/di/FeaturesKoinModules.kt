package com.example.lifesum.di

import androidx.lifecycle.SavedStateHandle
import com.example.lifesum.MainViewModel
import com.example.lifesum.common.food.api.IFoodRemoteDataSource
import com.example.lifesum.common.food.model.data.FoodRepository
import com.example.lifesum.common.food.model.data.IFoodRepository
import com.example.lifesum.common.food.model.usecases.GetFoodDetailUseCase
import com.example.lifesum.common.remote.ApplicationRemoteDataSource
import com.example.lifesum.common.remote.IApplicationRemoteDataSource
import com.example.lifesum.food.FoodDetailViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factory

object FeaturesKoinModules {
    private lateinit var appNetworkModule: Module
    private val foodModule: Module = module {
        factory<IFoodRepository> { FoodRepository(get()) }
        factory<GetFoodDetailUseCase>()
        viewModel { (handle: SavedStateHandle) ->
            FoodDetailViewModel(handle, get())
        }
    }

    val allModules: ArrayList<Module>
        get() {
            val list = arrayListOf<Module>()

            // general
            list.add(appNetworkModule)

            list.add(module { viewModel<MainViewModel>() })
            list.add(foodModule)

            return list
        }

    fun init() {
        // remote data source
        initAppNetworkModule()

    }

    private fun initAppNetworkModule() {
        appNetworkModule = module {
            single<IApplicationRemoteDataSource> { ApplicationRemoteDataSource(get()) }
            single<IFoodRemoteDataSource> { get<IApplicationRemoteDataSource>() }
        }
    }
}
