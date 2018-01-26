package com.sayler.ormliteimplementation.exception

/**
 * Created by sayler on 2018-01-25.
 *
 * Copyright 2018 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
sealed class CommunicationError : Throwable() {
    class NoDataSource: CommunicationError()
}