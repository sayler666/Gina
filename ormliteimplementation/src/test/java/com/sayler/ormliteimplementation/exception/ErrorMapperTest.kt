package com.sayler.ormliteimplementation.exception

import org.junit.Before
import org.junit.Test
import java.net.ConnectException
import org.junit.Assert.assertTrue

/**
 * Created by sayler on 27.01.2018.
 */
class ErrorMapperTest {
    lateinit var mapper: ErrorMapper

    @Before
    fun setup() {
        mapper = ErrorMapper()
    }

    @Test
    fun testMapsNoDataSourceError() {
        val error = ConnectException()

        val result = mapper.map(error)

        assertTrue(result is CommunicationError.NoDataSource)
    }

}