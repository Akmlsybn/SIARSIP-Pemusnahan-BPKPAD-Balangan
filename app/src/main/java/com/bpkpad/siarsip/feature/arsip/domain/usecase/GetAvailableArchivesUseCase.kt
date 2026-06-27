package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetAvailableArchivesUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<Arsip>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return repository.getAvailableArchives().map { archives ->
            archives.filter { archive ->
                // 1. Anti-Permanen-Bakar: Pastikan nasibAkhir == "MUSNAH"
                // Dan pastikan keterangan/retensiInaktif tidak bernilai "PERMANEN"
                val isPermanen = archive.nasibAkhir.equals("PERMANEN", ignoreCase = true) ||
                        archive.keterangan.equals("PERMANEN", ignoreCase = true) ||
                        archive.retensiInaktif.equals("PERMANEN", ignoreCase = true)

                if (isPermanen) {
                    false
                } else {
                    val docYear = archive.tahun.toIntOrNull() ?: 0
                    
                    // 2. Hybrid JRA System:
                    // Jika KEUANGAN (Prefix 900), otomatis 10 tahun (Aktif 2 + Inaktif 8 = 10)
                    // Jika NON-KEUANGAN (Prefix selain 900), hitung dinamis dari database (default 2 + 3 = 5 tahun jika tidak di-override)
                    val isKeuangan = archive.kode.startsWith("900")
                    
                    val totalRetention = if (isKeuangan) {
                        10
                    } else {
                        val activeYears = archive.retensiAktif.filter { it.isDigit() }.toIntOrNull() ?: 2
                        val inactiveYears = archive.retensiInaktif.filter { it.isDigit() }.toIntOrNull() ?: 3
                        activeYears + inactiveYears
                    }
                    
                    (docYear + totalRetention) <= currentYear
                }
            }
        }
    }
}
