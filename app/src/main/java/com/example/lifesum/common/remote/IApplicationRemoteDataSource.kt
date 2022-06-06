package com.example.lifesum.common.remote

import com.example.base.common.RemoteDataSource
import com.example.lifesum.common.food.api.IFoodRemoteDataSource

interface IApplicationRemoteDataSource :
    RemoteDataSource,
    IFoodRemoteDataSource