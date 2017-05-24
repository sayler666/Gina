package com.sayler.ormliteimplementation

import android.content.Context
import android.os.Environment
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.DataModule
import com.sayler.gina.domain.ObjectCreator
import com.sayler.gina.domain.interactor.DiaryInteractor
import com.sayler.gina.domain.presenter.diary.DiaryPresenter
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.creator.ObjectCreatorOrmLite
import com.sayler.ormliteimplementation.interactor.DiaryInteractorOrmLite
import dagger.Module
import dagger.Provides

import javax.inject.Singleton

@Module
class DataModuleOrmLite : DataModule() {

    @Singleton
    @Provides
    fun provideDataManagerOrmLite(context: Context): DataManager<*> {
        //TODO put this path somewhere
        //default path
        val dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/db.sqlite"

        val ormLiteManager = OrmLiteManager(context)
        ormLiteManager.setSourceFile(dbPath)
        return ormLiteManager
    }

    @Singleton
    @Provides
    fun provideDayCreator(): ObjectCreator {
        return ObjectCreatorOrmLite()
    }

    @Singleton
    @Provides
    fun provideDaysDataProvider(context: Context, ormLiteManager: DataManager<*>): DaysDataProvider {
        val daysDataProvider = DaysDataProvider(context)
        (ormLiteManager as OrmLiteManager).add(daysDataProvider)
        return daysDataProvider
    }

    @Singleton
    @Provides
    fun provideAttachmentsDataProvider(context: Context, ormLiteManager: DataManager<*>): AttachmentsDataProvider {
        val attachmentsDataProvider = AttachmentsDataProvider(context)
        (ormLiteManager as OrmLiteManager).add(attachmentsDataProvider)
        return attachmentsDataProvider
    }

    @Provides
    fun provideDaysInteractorOrmLite(iRxAndroidTransformer: IRxAndroidTransformer, daysDataProvider: DaysDataProvider, attachmentsDataProvider: AttachmentsDataProvider, dataManager: DataManager<*>): DiaryInteractor {
        return DiaryInteractorOrmLite(iRxAndroidTransformer, daysDataProvider, attachmentsDataProvider, dataManager)
    }

    @Provides
    fun provideDaysPresenter(context: Context, diaryInteractor: DiaryInteractor): DiaryPresenter {
        return DiaryPresenter(context, diaryInteractor)
    }

}