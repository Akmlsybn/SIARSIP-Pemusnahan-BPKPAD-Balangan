package com.bpkpad.peminjaman.core.notification

import android.content.Context
import com.bpkpad.peminjaman.core.database.dao.TransaksiDao
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class OverdueWorker @Inject constructor(
    private val transaksiDao: TransaksiDao,
    private val notificationHelper: NotificationHelper
) {
    suspend fun checkOverdue(context: Context) {
        try {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val overdueList = transaksiDao.getOverdueSync(today)

            if (overdueList.isNotEmpty()) {
                notificationHelper.showOverdueNotification(overdueList.size)
            }
        } catch (e: Exception) {
            android.util.Log.e("OVERDUE_WORKER", "Failed: ${e.message}", e)
        }
    }
}
