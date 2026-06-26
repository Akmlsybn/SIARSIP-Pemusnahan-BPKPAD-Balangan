package com.bpkpad.siarsip.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        UserEntity::class,
        ArsipEntity::class,
        BerkasUsulMusnahEntity::class,
        BeritaAcaraEntity::class,
        PenandatanganEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun arsipDao(): ArsipDao
    abstract fun berkasUsulMusnahDao(): BerkasUsulMusnahDao
    abstract fun beritaAcaraDao(): BeritaAcaraDao
    abstract fun penandatanganDao(): PenandatanganDao
    abstract fun auditLogDao(): AuditLogDao
}