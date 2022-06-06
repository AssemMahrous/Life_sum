package com.example.lifesum.common.food.model.data

import com.example.base.platform.BaseRepository
import com.example.lifesum.common.food.api.IFoodRemoteDataSource
import com.example.lifesum.common.food.model.Mapper.toFood


open class FoodRepository(
    remoteDataSource: IFoodRemoteDataSource,
) : BaseRepository<IFoodRemoteDataSource>(
    remoteDataSource
),
    IFoodRepository {
    override suspend fun getFoodData(foodId: Int) =
        safeApiCall2(
            networkCall = { remoteDataSource.foodApi.getFoodData(foodId) },
            successHandler = { it.data?.toFood()!! })
}

