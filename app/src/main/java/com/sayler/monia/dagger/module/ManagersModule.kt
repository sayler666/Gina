/**
 * Created by sayler on 2016-11-22.
 * <p>

 */
package com.sayler.monia.dagger.module;

import com.sayler.monia.attachment.AttachmentManager
import com.sayler.monia.attachment.AttachmentManagerContract
import com.sayler.monia.domain.ObjectCreator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ManagersModule {

    @Singleton
    @Provides
    fun provideAttachmentsManager(objectCreator: ObjectCreator): AttachmentManagerContract.Presenter {
        return AttachmentManager(objectCreator)
    }
}
