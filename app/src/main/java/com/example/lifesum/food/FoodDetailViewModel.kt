package com.example.lifesum.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.example.base.platform.BaseViewModel
import com.example.lifesum.common.food.model.Mapper.toFoodView
import com.example.lifesum.common.food.model.entites.view.FoodView
import com.example.lifesum.common.food.model.usecases.GetFoodDetailUseCase

class FoodDetailViewModel(
    private val state: SavedStateHandle,
    private val getFoodDetailUseCase: GetFoodDetailUseCase
) : BaseViewModel() {
    fun getData() {
        state.set(FETCHING_KEY, true)
        wrapBlockingOperation {
            handleResult(getFoodDetailUseCase()) {
                state.set(FOOD_KEY, it.data.toFoodView())
                state.set(FETCHING_KEY, false)
            }
        }
    }

    fun getSavedData(): LiveData<FoodView?> {
        return state.getLiveData(FOOD_KEY)
    }

    fun getFetchState(): Boolean? {
        return state.get<Boolean>(FETCHING_KEY)
    }

    companion object {
        private const val FETCHING_KEY = "fetching"
        private const val FOOD_KEY = "food"
    }
}