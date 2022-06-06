package com.example.lifesum.common.food.api

import com.example.base.common.BaseResponse
import com.example.lifesum.common.food.api.API.FOOD.PARAM_FOOD
import com.example.lifesum.common.food.api.API.FOOD.PATH_CODE_TEST
import com.example.lifesum.common.food.model.entites.response.FoodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {

    @GET(PATH_CODE_TEST)
    suspend fun getFoodData(@Query(PARAM_FOOD) foodId: Int): Response<BaseResponse<FoodResponse>>
}