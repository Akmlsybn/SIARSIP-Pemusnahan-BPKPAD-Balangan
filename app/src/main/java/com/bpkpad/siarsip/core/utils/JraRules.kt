package com.bpkpad.siarsip.core.utils

import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import java.util.Calendar

/**
 * Single Source of Truth (SSOT) untuk logika Jadwal Retensi Arsip (JRA)
 * dan penentuan kelayakan pemusnahan dokumen di lingkungan BPKPAD Balangan.
 *
 * Aturan JRA:
 * 1. Arsip Keuangan (Kode diawali "900"):
 *    Masa retensi = 10 tahun (2 tahun aktif + 8 tahun inaktif).
 * 2. Arsip Non-Keuangan (Kode selain "900"):
 *    Masa retensi = aktif + inaktif dari data (bawaan: 2 tahun aktif + 3 tahun inaktif = 5 tahun).
 * 3. Arsip Layak Musnah jika:
 *    - Nasib akhir = "MUSNAH"
 *    - Tidak berstatus/keterangan "PERMANEN"
 *    - (tahun dokumen + total retensi) <= tahun berjalan
 */
object JraRules {
    const val KEUANGAN_RETENTION_YEARS = 10
    const val NON_KEUANGAN_DEFAULT_RETENTION_YEARS = 5

    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun getCutoffYearKeuangan(currentYear: Int = getCurrentYear()): Int =
        currentYear - KEUANGAN_RETENTION_YEARS

    fun getCutoffYearNonKeuangan(currentYear: Int = getCurrentYear()): Int =
        currentYear - NON_KEUANGAN_DEFAULT_RETENTION_YEARS

    fun isKeuangan(kode: String?, sumber: String? = null): Boolean {
        if (sumber.equals("Keuangan", ignoreCase = true)) return true
        return kode?.startsWith("900") == true
    }

    /**
     * Memeriksa apakah suatu arsip layak untuk diajukan dalam Berkas Usul Musnah (BUM).
     */
    fun isEligibleForDisposal(
        kode: String?,
        tahun: String?,
        nasibAkhir: String?,
        keterangan: String? = null,
        retensiAktif: String? = null,
        retensiInaktif: String? = null,
        sumber: String? = null,
        currentYear: Int = getCurrentYear()
    ): Boolean {
        // 1. Anti-Permanen: tidak boleh berstatus PERMANEN pada nasib akhir, keterangan, atau retensi
        val isPermanen = nasibAkhir.equals("PERMANEN", ignoreCase = true) ||
                keterangan.equals("PERMANEN", ignoreCase = true) ||
                retensiInaktif.equals("PERMANEN", ignoreCase = true)
        if (isPermanen) return false

        // 2. Nasib Akhir harus MUSNAH
        if (!nasibAkhir.equals("MUSNAH", ignoreCase = true)) {
            return false
        }

        val docYear = tahun?.toIntOrNull() ?: return false

        // 3. Hitung total retensi sesuai aturan JRA
        val isDocKeuangan = isKeuangan(kode, sumber)
        val totalRetention = if (isDocKeuangan) {
            KEUANGAN_RETENTION_YEARS
        } else {
            val active = retensiAktif?.filter { it.isDigit() }?.toIntOrNull() ?: 2
            val inactive = retensiInaktif?.filter { it.isDigit() }?.toIntOrNull() ?: 3
            active + inactive
        }

        return (docYear + totalRetention) <= currentYear
    }

    fun isEligibleForDisposal(archive: Arsip, currentYear: Int = getCurrentYear()): Boolean {
        return isEligibleForDisposal(
            kode = archive.kode,
            tahun = archive.tahun,
            nasibAkhir = archive.nasibAkhir,
            keterangan = archive.keterangan,
            retensiAktif = archive.retensiAktif,
            retensiInaktif = archive.retensiInaktif,
            sumber = archive.sumber,
            currentYear = currentYear
        )
    }
}
