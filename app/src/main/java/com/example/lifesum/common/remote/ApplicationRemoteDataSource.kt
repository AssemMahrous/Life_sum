package com.example.lifesum.common.remote

import com.example.base.common.RemoteDataSourceImpl
import com.example.lifesum.common.food.api.FoodApi
import retrofit2.Retrofit

class ApplicationRemoteDataSource(override val retrofit: Retrofit) : RemoteDataSourceImpl(retrofit),
    IApplicationRemoteDataSource {
    override val foodApi: FoodApi = retrofit.create(FoodApi::class.java)
}