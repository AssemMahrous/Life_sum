package com.example.base.common

import retrofit2.Retrofit

open class RemoteDataSourceImpl(open val retrofit: Retrofit) : RemoteDataSource