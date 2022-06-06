package com.example.base.platform

import com.example.base.utils.Loading


class RetryOperation(val showLoading: Loading, val function: suspend () -> Any)