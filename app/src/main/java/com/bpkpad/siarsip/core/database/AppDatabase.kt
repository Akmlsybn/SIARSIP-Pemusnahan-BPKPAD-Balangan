package com.bpkpad.siarsip.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bpkpad.siarsip.core.database.dao.UserDao
import com.bpkpad.siarsip.core.database.dao.ArsipDao
import com.bpkpad.siarsip.core.database.dao.BerkasUsulMusnahDao
import com.bpkpad.siarsip.core.database.dao.BeritaAcaraDao
import com.bpkpad.siarsip.core.database.dao.PenandatanganDao
import com.bpkpad.siarsip.core.database.dao.AuditLogDao
import com.bpkpad.siarsip.core.database.entity.UserEntity
import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import com.bpkpad.siarsip.core.database.entity.BerkasUsulMusnahEntity
import com.bpkpad.siarsip.core.database.entity.BeritaAcaraEntity
import com.bpkpad.siarsip.core.database.entity.PenandatanganEntity
import com.bpkpad.siarsip.core.database.entity.AuditLogEntity

// Modul Keuangan Entities & DAOs
import com.example.arsipbpkpad.data.local.entity.ArchiveEntity as KeuanganArchiveEntity
import com.example.arsipbpkpad.data.local.entity.ClassificationCodeEntity
import com.example.arsipbpkpad.data.local.entity.StagingArchiveEntity
import com.example.arsipbpkpad.data.local.entity.StagingBoxEntity
import com.example.arsipbpkpad.data.local.dao.ArchiveDao as KeuanganArchiveDao
import com.example.arsipbpkpad.data.local.dao.ClassificationCodeDao as KeuanganClassificationCodeDao
import com.example.arsipbpkpad.data.local.dao.StagingArchiveDao as KeuanganStagingArchiveDao
import com.example.arsipbpkpad.data.local.converter.DatabaseConverters as KeuanganConverters

@Database(
    entities = [
        // Main App Pemusnahan
        UserEntity::class,
        ArsipEntity::class,
        BerkasUsulMusnahEntity::class,
        BeritaAcaraEntity::class,
        PenandatanganEntity::class,
        AuditLogEntity::class,

        // Modul Keuangan
        KeuanganArchiveEntity::class,
        ClassificationCodeEntity::class,
        StagingArchiveEntity::class,
        StagingBoxEntity::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(KeuanganConverters::class)
abstract class AppDatabase : RoomDatabase() {
    // Main App DAOs
    abstract fun userDao(): UserDao
    abstract fun arsipDao(): ArsipDao
    abstract fun berkasUsulMusnahDao(): BerkasUsulMusnahDao
    abstract fun beritaAcaraDao(): BeritaAcaraDao
    abstract fun penandatanganDao(): PenandatanganDao
    abstract fun auditLogDao(): AuditLogDao

    // Keuangan DAOs
    abstract fun keuanganArchiveDao(): KeuanganArchiveDao
    abstract fun keuanganClassificationCodeDao(): KeuanganClassificationCodeDao
    abstract fun keuanganStagingArchiveDao(): KeuanganStagingArchiveDao
}