package com.sayler.ormliteimplementation

import android.content.Context
import android.os.Environment
import com.sayler.gina.domain.DataManager
import com.sayler.gina.domain.DataModule
import com.sayler.gina.domain.ObjectCreator
import com.sayler.gina.domain.interactor.DiaryInteractor
import com.sayler.gina.domain.presenter.day.DayContract
import com.sayler.gina.domain.presenter.diary.DiaryContract
import com.sayler.gina.domain.presenter.diary.DiaryPresenter
import com.sayler.gina.domain.presenter.list.ShowListContract
import com.sayler.gina.domain.presenter.list.usecase.CalculateStatisticsUseCase
import com.sayler.gina.domain.presenter.list.usecase.CheckIfRememberedSourceUseCase
import com.sayler.gina.domain.presenter.list.usecase.RememberSourceUseCase
import com.sayler.gina.domain.presenter.list.usecase.SetNewSourceUseCase
import com.sayler.gina.domain.rx.IRxAndroidTransformer
import com.sayler.ormliteimplementation.creator.ObjectCreatorOrmLite
import com.sayler.ormliteimplementation.day.presenter.DayPresenter
import com.sayler.ormliteimplementation.day.usecase.FindDayByIdUseCase
import com.sayler.ormliteimplementation.day.usecase.FindNextDayAfterDateUseCase
import com.sayler.ormliteimplementation.day.usecase.FindPreviousDayAfterDateUseCase
import com.sayler.ormliteimplementation.exception.OrmLiteErrorMapper
import com.sayler.ormliteimplementation.interactor.DiaryInteractorOrmLite
import com.sayler.ormliteimplementation.list.presenter.ShowListPresenter
import com.sayler.ormliteimplementation.list.usecase.FindByTextUseCase
import com.sayler.ormliteimplementation.list.usecase.GetAllUseCase
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
    fun provideDaysPresenter(diaryInteractor: DiaryInteractor): DiaryContract.Presenter {
        return DiaryPresenter(diaryInteractor)
    }

    //error mapper

    @Provides
    fun provideOrmLiteErrorMapper(): OrmLiteErrorMapper {
        return OrmLiteErrorMapper()
    }

    //show list presenter

    @Provides
    fun provideGetAllUseCase(daysDataProvider: DaysDataProvider,
                             ormLiteErrorMapper: OrmLiteErrorMapper)
            : GetAllUseCase {
        return GetAllUseCase(daysDataProvider, ormLiteErrorMapper)
    }

    @Provides
    fun provideFindByTextUseCase(daysDataProvider: DaysDataProvider,
                                 ormLiteErrorMapper: OrmLiteErrorMapper)
            : FindByTextUseCase {
        return FindByTextUseCase(daysDataProvider, ormLiteErrorMapper)
    }

    @Provides
    fun provideShowListPresenter(iRxAndroidTransformer: IRxAndroidTransformer,
                                 getAllUseCase: GetAllUseCase,
                                 findByTextUseCase: FindByTextUseCase,
                                 statisticsUseCase: CalculateStatisticsUseCase,
                                 settingsSetNewSourceUseCase: SetNewSourceUseCase,
                                 checkIfRememberedSourceUseCase: CheckIfRememberedSourceUseCase,
                                 rememberSourceUseCase: RememberSourceUseCase
    )
            : ShowListContract.Presenter {
        return ShowListPresenter(getAllUseCase,
                findByTextUseCase,
                statisticsUseCase,
                checkIfRememberedSourceUseCase,
                settingsSetNewSourceUseCase,
                rememberSourceUseCase,
                iRxAndroidTransformer)
    }

    //day presenter

    @Provides
    fun provideFindDayByIdUseCase(daysDataProvider: DaysDataProvider,
                                  ormLiteErrorMapper: OrmLiteErrorMapper)
            : FindDayByIdUseCase {
        return FindDayByIdUseCase(daysDataProvider, ormLiteErrorMapper)
    }

    @Provides
    fun provideFindNextDayAfterDateUseCase(daysDataProvider: DaysDataProvider,
                                           ormLiteErrorMapper: OrmLiteErrorMapper)
            : FindNextDayAfterDateUseCase {
        return FindNextDayAfterDateUseCase(daysDataProvider, ormLiteErrorMapper)
    }

    @Provides
    fun provideFindPreviousDayAfterDateUseCase(daysDataProvider: DaysDataProvider,
                                               ormLiteErrorMapper: OrmLiteErrorMapper)
            : FindPreviousDayAfterDateUseCase {
        return FindPreviousDayAfterDateUseCase(daysDataProvider, ormLiteErrorMapper)
    }

    @Provides
    fun provideDayPresenter(iRxAndroidTransformer: IRxAndroidTransformer,
                            findDayByIdUseCase: FindDayByIdUseCase,
                            findNextDayAfterDateUseCase: FindNextDayAfterDateUseCase,
                            findPreviousDayAfterDateUseCase: FindPreviousDayAfterDateUseCase)
            : DayContract.Presenter {
        return DayPresenter(findDayByIdUseCase, findNextDayAfterDateUseCase, findPreviousDayAfterDateUseCase, iRxAndroidTransformer)
    }

}