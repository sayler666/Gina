/**
 * Created by sayler on 2016-11-22.
 * <p>

 */
package com.sayler.gina.dagger.module;

import com.sayler.gina.attachment.AttachmentManager
import com.sayler.gina.attachment.AttachmentManagerContract
import com.sayler.gina.domain.ObjectCreator
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
