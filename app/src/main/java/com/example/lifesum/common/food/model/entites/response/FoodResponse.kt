package com.example.lifesum.common.food.model.entites.response

import com.google.gson.annotations.SerializedName

data class FoodResponse(
    @SerializedName("title") val title: String,
    @SerializedName("calories") val calories: Int,
    @SerializedName("carbs") val carbs: Double,
    @SerializedName("protein") val protein: Double,
    @SerializedName("fat") val fat: Double,
    @SerializedName("saturatedfat") val saturatedfat: Double,
    @SerializedName("unsaturatedfat") val unsaturatedfat: Double,
    @SerializedName("fiber") val fiber: Double,
    @SerializedName("cholesterol") val cholesterol: Double,
    @SerializedName("sugar") val sugar: Double,
    @SerializedName("potassium") val potassium: Double,
    @SerializedName("gramsperserving") val gramsperserving: Double,
    @SerializedName("pcstext") val pcstext: String,
)
