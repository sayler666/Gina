package com.sayler.ormliteimplementation.exception

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.ConnectException
import java.sql.SQLSyntaxErrorException
import java.sql.SQLTimeoutException

/**
 * Created by sayler on 27.01.2018.
 */
class OrmLiteErrorMapperTest {
    lateinit var mapper: OrmLiteErrorMapper

    @Before
    fun setup() {
        mapper = OrmLiteErrorMapper()
    }

    @Test
    fun testMapsTimeoutError() {
        val error = SQLTimeoutException()

        val result = mapper.map(error)

        assertTrue(result is OrmLiteError.TimeoutError)
    }

    @Test
    fun testMapsSyntaxError() {
        val error = SQLSyntaxErrorException()

        val result = mapper.map(error)

        assertTrue(result is OrmLiteError.SyntaxError)
    }

    @Test
    fun testMapsNoDataIOOBError() {
        val error = IndexOutOfBoundsException()

        val result = mapper.map(error)

        assertTrue(result is OrmLiteError.NoDataError)
    }

    @Test
    fun testMapsNoDataNPEError() {
        val error = NullPointerException()

        val result = mapper.map(error)

        assertTrue(result is OrmLiteError.NoDataError)
    }

    @Test
    fun testMapsNoDataSourceError() {
        val error = ConnectException()

        val result = mapper.map(error)

        assertTrue(result is CommunicationError.NoDataSource)
    }
}