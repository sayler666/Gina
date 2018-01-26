package com.sayler.ormliteimplementation.exception

import java.sql.SQLSyntaxErrorException
import java.sql.SQLTimeoutException

/**
 * Created by sayler on 2018-01-25.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
class OrmLiteErrorMapper : ErrorMapper() {
    override fun map(error: Throwable): Throwable {
        when (error) {
            is SQLTimeoutException -> return handleTimeoutException(error)
            is SQLSyntaxErrorException -> return handleSQLSyntaxError(error)
            is IndexOutOfBoundsException -> return handleNoDataException(error)
        }
        return super.map(error)
    }

    private fun handleSQLSyntaxError(error: SQLSyntaxErrorException): Throwable {
        return OrmLiteError.SyntaxError()
    }

    private fun handleTimeoutException(error: SQLTimeoutException): Throwable {
        return OrmLiteError.TimeoutError()
    }

    private fun handleNoDataException(error: IndexOutOfBoundsException): Throwable {
        return OrmLiteError.NoDataError()
    }
}