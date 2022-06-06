package com.example.lifesum.common.food.model.usecases

import com.example.base.platform.BaseUseCase
import com.example.lifesum.common.food.model.data.IFoodRepository

class GetFoodDetailUseCase(repository: IFoodRepository) :
    BaseUseCase<IFoodRepository>(repository) {
    suspend operator fun invoke() = repository.getFoodData((1..200).random())
}