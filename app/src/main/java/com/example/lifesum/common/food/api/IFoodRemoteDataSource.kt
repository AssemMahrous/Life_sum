package com.example.lifesum.common.food.api


import com.example.base.common.RemoteDataSource

interface IFoodRemoteDataSource : RemoteDataSource {
    val foodApi: FoodApi
}
