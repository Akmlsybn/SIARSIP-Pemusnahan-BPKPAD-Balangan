package com.bpkpad.siarsip.core.di

import android.content.Context
import androidx.room.Room
import com.bpkpad.siarsip.core.database.AppDatabase
import com.bpkpad.siarsip.core.database.DatabaseCallback
import com.bpkpad.siarsip.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "siarsip_database"
        )
        .addCallback(DatabaseCallback())
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideArsipDao(appDatabase: AppDatabase): ArsipDao {
        return appDatabase.arsipDao()
    }

    @Provides
    fun provideBerkasUsulMusnahDao(appDatabase: AppDatabase): BerkasUsulMusnahDao {
        return appDatabase.berkasUsulMusnahDao()
    }

    @Provides
    fun provideBeritaAcaraDao(appDatabase: AppDatabase): BeritaAcaraDao {
        return appDatabase.beritaAcaraDao()
    }

    @Provides
    fun providePenandatanganDao(appDatabase: AppDatabase): PenandatanganDao {
        return appDatabase.penandatanganDao()
    }

    @Provides
    fun provideAuditLogDao(appDatabase: AppDatabase): AuditLogDao {
        return appDatabase.auditLogDao()
    }

    @Provides
    fun provideKeuanganArchiveDao(appDatabase: AppDatabase): com.example.arsipbpkpad.data.local.dao.ArchiveDao {
        return appDatabase.keuanganArchiveDao()
    }

    @Provides
    fun provideKeuanganClassificationCodeDao(appDatabase: AppDatabase): com.example.arsipbpkpad.data.local.dao.ClassificationCodeDao {
        return appDatabase.keuanganClassificationCodeDao()
    }

    @Provides
    fun provideKeuanganStagingArchiveDao(appDatabase: AppDatabase): com.example.arsipbpkpad.data.local.dao.StagingArchiveDao {
        return appDatabase.keuanganStagingArchiveDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): android.content.SharedPreferences {
        return context.getSharedPreferences("siarsip_preferences", Context.MODE_PRIVATE)
    }
}