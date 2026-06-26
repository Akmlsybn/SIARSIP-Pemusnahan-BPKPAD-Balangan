package com.bpkpad.siarsip.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bpkpad.siarsip.core.database.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class DatabaseCallback(
    private val databaseProvider: () -> AppDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = databaseProvider().userDao()
            if (userDao.countUsers() == 0) {
                userDao.insertUser(
                    UserEntity(
                        username = "admin",
                        passwordHash = sha256("admin123")
                    )
                )
            }
        }
    }

    private fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}