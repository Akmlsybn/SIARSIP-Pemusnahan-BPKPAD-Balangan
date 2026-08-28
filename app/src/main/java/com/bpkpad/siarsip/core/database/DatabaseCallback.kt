package com.bpkpad.siarsip.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.security.MessageDigest

class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedData(db)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        ensureDataSeeded(db)
    }

    private fun ensureDataSeeded(db: SupportSQLiteDatabase) {
        try {
            val cursor = db.query("SELECT COUNT(*) FROM users WHERE username = 'admin'")
            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()

            if (count == 0) {
                seedData(db)
            }
        } catch (e: Exception) {
            // Ignore if check fails
        }
    }

    private fun seedData(db: SupportSQLiteDatabase) {
        // Seed default admin user if empty
        safeExec(db, "INSERT OR IGNORE INTO users (username, passwordHash) VALUES ('admin', '${sha256("admin123")}')")
    }

    private fun safeExec(db: SupportSQLiteDatabase, sql: String) {
        try {
            db.execSQL(sql)
        } catch (_: Exception) {}
    }

    private fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}