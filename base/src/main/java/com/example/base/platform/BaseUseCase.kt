package com.example.base.platform

abstract class BaseUseCase<Repository : IBaseRepository>(val repository: Repository)