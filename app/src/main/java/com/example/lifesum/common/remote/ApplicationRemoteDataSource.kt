package com.example.lifesum.common.remote

import com.example.base.common.RemoteDataSourceImpl
import retrofit2.Retrofit

class ApplicationRemoteDataSource(override val retrofit: Retrofit) : RemoteDataSourceImpl(retrofit),
    IApplicationRemoteDataSource {
}