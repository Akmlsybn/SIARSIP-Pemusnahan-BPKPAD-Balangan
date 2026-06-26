package com.bpkpad.siarsip.core.di

import com.bpkpad.siarsip.feature.arsip.data.repository.ArsipRepositoryImpl
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ArsipModule {

    @Binds
    @Singleton
    abstract fun bindArsipRepository(
        impl: ArsipRepositoryImpl
    ): ArsipRepository
}
