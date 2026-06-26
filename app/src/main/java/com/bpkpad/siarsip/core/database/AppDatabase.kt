package com.bpkpad.siarsip.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bpkpad.siarsip.core.database.dao.UserDao
import com.bpkpad.siarsip.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}