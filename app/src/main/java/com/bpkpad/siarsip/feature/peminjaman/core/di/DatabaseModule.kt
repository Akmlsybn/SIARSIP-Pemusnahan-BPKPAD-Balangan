package com.bpkpad.peminjaman.core.di

import android.content.Context
import androidx.room.Room
import com.bpkpad.peminjaman.core.database.AppDatabase as PeminjamanAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PeminjamanDatabaseModule {

    @Provides
    @Singleton
    fun providePeminjamanAppDatabase(@ApplicationContext context: Context): PeminjamanAppDatabase {
        return Room.databaseBuilder(
            context,
            PeminjamanAppDatabase::class.java,
            "peminjaman_arsip.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideUserDao(db: PeminjamanAppDatabase) = db.userDao()
    @Provides fun provideInstansiDao(db: PeminjamanAppDatabase) = db.instansiDao()
    @Provides fun provideMasterDokumenDao(db: PeminjamanAppDatabase) = db.masterDokumenDao()
    @Provides fun provideTransaksiDao(db: PeminjamanAppDatabase) = db.transaksiDao()
    @Provides fun provideDetailPeminjamanDao(db: PeminjamanAppDatabase) = db.detailPeminjamanDao()
    @Provides fun providePerpanjanganDao(db: PeminjamanAppDatabase) = db.perpanjanganDao()
    @Provides fun provideAuditLogDao(db: PeminjamanAppDatabase) = db.auditLogDao()
}
