package com.bpkpad.arsip.core.di

import android.content.Context
import androidx.room.Room
import com.bpkpad.arsip.core.data.local.AppDatabase as NonKeuanganAppDatabase
import com.bpkpad.arsip.core.data.local.dao.TempDocumentDao
import com.bpkpad.arsip.core.data.local.dao.UserDao as NonKeuanganUserDao
import com.bpkpad.arsip.core.data.local.dao.ArchiveDocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NonKeuanganDatabaseModule {

    @Provides
    @Singleton
    @Named("nonKeuanganDb")
    fun provideNonKeuanganAppDatabase(
        @ApplicationContext context: Context
    ): NonKeuanganAppDatabase {
        return Room.databaseBuilder(
            context,
            NonKeuanganAppDatabase::class.java,
            "arsip_non_keuangan_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTempDocumentDao(@Named("nonKeuanganDb") database: NonKeuanganAppDatabase): TempDocumentDao {
        return database.tempDocumentDao()
    }

    @Provides
    @Named("nonKeuanganUserDao")
    fun provideNonKeuanganUserDao(@Named("nonKeuanganDb") database: NonKeuanganAppDatabase): NonKeuanganUserDao {
        return database.userDao()
    }

    @Provides
    fun provideArchiveDocumentDao(@Named("nonKeuanganDb") database: NonKeuanganAppDatabase): ArchiveDocumentDao {
        return database.archiveDocumentDao()
    }
}