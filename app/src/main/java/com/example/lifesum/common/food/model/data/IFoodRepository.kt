package com.example.lifesum.common.food.model.data

import com.example.base.platform.IBaseRepository
import com.example.base.utils.Result
import com.example.lifesum.common.food.model.domain.Food


interface IFoodRepository : IBaseRepository {
    suspend fun getFoodData(foodId: Int): Result<Food>
}