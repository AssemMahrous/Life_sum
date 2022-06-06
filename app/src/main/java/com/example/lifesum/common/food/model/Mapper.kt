package com.example.lifesum.common.food.model

import com.example.lifesum.common.food.model.domain.Food
import com.example.lifesum.common.food.model.entites.response.FoodResponse
import com.example.lifesum.common.food.model.entites.view.FoodView

object Mapper {

    fun FoodResponse.toFood() = Food(
        title = title,
        fat = fat,
        saturatedfat = saturatedfat,
        unsaturatedfat = unsaturatedfat,
        calories = calories,
        carbs = carbs,
        cholesterol = cholesterol,
        fiber = fiber,
        gramsperserving = gramsperserving,
        pcstext = pcstext,
        potassium = potassium,
        protein = protein,
        sugar = sugar
    )

    fun Food.toFoodView() = FoodView(
        title = title,
        fat = fat,
        saturatedfat = saturatedfat,
        unsaturatedfat = unsaturatedfat,
        calories = calories,
        carbs = carbs,
        cholesterol = cholesterol,
        fiber = fiber,
        gramsperserving = gramsperserving,
        pcstext = pcstext,
        potassium = potassium,
        protein = protein,
        sugar = sugar
    )
}