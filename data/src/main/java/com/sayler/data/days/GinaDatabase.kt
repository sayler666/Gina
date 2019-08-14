package com.sayler.data.days

import com.sayler.data.days.dao.AttachmentDao
import com.sayler.data.days.dao.DayDao

interface GinaDatabase {
    fun dayDao(): DayDao
    fun attachmentDao(): AttachmentDao
}
