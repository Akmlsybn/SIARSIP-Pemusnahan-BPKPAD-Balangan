package com.bpkpad.peminjaman.core.database

import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val db: AppDatabase
) {
    fun seedIfEmpty(scope: CoroutineScope) {
        // Disabled dummy seeder. System operates using Supabase Cloud data and Room cache.
    }
}
