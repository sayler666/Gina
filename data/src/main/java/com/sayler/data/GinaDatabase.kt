package com.sayler.data

import com.sayler.data.dao.DayDao

interface GinaDatabase {
    fun dayDao(): DayDao
}
