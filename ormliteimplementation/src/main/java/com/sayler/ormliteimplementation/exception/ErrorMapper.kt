package com.sayler.ormliteimplementation.exception

import java.net.ConnectException

/**
 * Created by sayler on 2018-01-25.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */

open class ErrorMapper{
    open fun map(error: Throwable):Throwable{
        return when(error) {
            is ConnectException -> handleConnectException(error)
            else -> handleUnrecognizedException(error)
        }
    }

    private fun handleConnectException(error: ConnectException): Throwable {
        return CommunicationError.NoDataSource()
    }

    private fun handleUnrecognizedException(error: Throwable): Throwable {
        return error
    }
}