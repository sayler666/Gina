package com.sayler.app2.data

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.sayler.data.GinaRoomDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModuleBinds::class])
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context): GinaRoomDatabase {
        // TODO provide db location
        val builder = Room.databaseBuilder(context, GinaRoomDatabase::class.java, "shows.db")
                .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Provides
    fun provideDayDao(db: com.sayler.data.GinaDatabase) = db.dayDao()

}

@Module
abstract class DatabaseModuleBinds {
    @Binds
    abstract fun bindGinaDatabase(roomDatabase: GinaRoomDatabase): com.sayler.data.GinaDatabase

}
