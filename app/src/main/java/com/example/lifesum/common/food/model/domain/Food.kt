package com.example.lifesum.common.food.model.domain

data class Food(
    val title: String,
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val saturatedfat: Double,
    val unsaturatedfat: Double,
    val fiber: Double,
    val cholesterol: Double,
    val sugar: Double,
    val potassium: Double,
    val gramsperserving: Double,
    val pcstext: String,
)
