package com.bpkpad.siarsip

import com.bpkpad.siarsip.core.utils.JraRules
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JraRulesTest {

    private val currentYear = 2026

    @Test
    fun testCutoffYears() {
        assertEquals(2016, JraRules.getCutoffYearKeuangan(currentYear))
        assertEquals(2021, JraRules.getCutoffYearNonKeuangan(currentYear))
    }

    @Test
    fun testKeuanganArchiveEligibility() {
        // SP2D tahun 2015 -> 2015 + 10 = 2025 <= 2026 (Layak)
        val arsip2015 = createArchive(kode = "900.1.3.1", tahun = "2015", nasibAkhir = "MUSNAH", sumber = "Keuangan")
        assertTrue(JraRules.isEligibleForDisposal(arsip2015, currentYear))

        // SP2D tahun 2016 -> 2016 + 10 = 2026 <= 2026 (Layak)
        val arsip2016 = createArchive(kode = "900.1.3.1", tahun = "2016", nasibAkhir = "MUSNAH", sumber = "Keuangan")
        assertTrue(JraRules.isEligibleForDisposal(arsip2016, currentYear))

        // SP2D tahun 2017 -> 2017 + 10 = 2027 > 2026 (Belum Layak)
        val arsip2017 = createArchive(kode = "900.1.3.1", tahun = "2017", nasibAkhir = "MUSNAH", sumber = "Keuangan")
        assertFalse(JraRules.isEligibleForDisposal(arsip2017, currentYear))

        // SP2D tahun 2020 -> 2020 + 10 = 2030 > 2026 (Belum Layak)
        val arsip2020 = createArchive(kode = "900.1.3.1", tahun = "2020", nasibAkhir = "MUSNAH", sumber = "Keuangan")
        assertFalse(JraRules.isEligibleForDisposal(arsip2020, currentYear))
    }

    @Test
    fun testNonKeuanganArchiveEligibility() {
        // Non-keuangan tahun 2021 -> 2021 + 5 = 2026 <= 2026 (Layak)
        val arsip2021 = createArchive(kode = "005.1", tahun = "2021", nasibAkhir = "MUSNAH", sumber = "Non-Keuangan")
        assertTrue(JraRules.isEligibleForDisposal(arsip2021, currentYear))

        // Non-keuangan tahun 2022 -> 2022 + 5 = 2027 > 2026 (Belum Layak)
        val arsip2022 = createArchive(kode = "005.1", tahun = "2022", nasibAkhir = "MUSNAH", sumber = "Non-Keuangan")
        assertFalse(JraRules.isEligibleForDisposal(arsip2022, currentYear))
    }

    @Test
    fun testPermanenArchiveAlwaysRejected() {
        // Nasib akhir PERMANEN
        val permanenNasib = createArchive(kode = "900.1.3.1", tahun = "2010", nasibAkhir = "PERMANEN")
        assertFalse(JraRules.isEligibleForDisposal(permanenNasib, currentYear))

        // Keterangan PERMANEN
        val permanenKet = createArchive(kode = "900.1.3.1", tahun = "2010", nasibAkhir = "MUSNAH", keterangan = "PERMANEN")
        assertFalse(JraRules.isEligibleForDisposal(permanenKet, currentYear))

        // Retensi Inaktif PERMANEN
        val permanenRetensi = createArchive(kode = "900.1.3.1", tahun = "2010", nasibAkhir = "MUSNAH", retensiInaktif = "PERMANEN")
        assertFalse(JraRules.isEligibleForDisposal(permanenRetensi, currentYear))
    }

    private fun createArchive(
        kode: String,
        tahun: String,
        nasibAkhir: String,
        sumber: String = "Keuangan",
        keterangan: String = "",
        retensiAktif: String = "2 Tahun",
        retensiInaktif: String = if (sumber == "Keuangan") "8 Tahun" else "3 Tahun"
    ): Arsip {
        return Arsip(
            id = "test-id",
            kode = kode,
            fullKode = kode,
            deskripsi = "Dokumen Uji",
            tahun = tahun,
            tingkat = "Asli",
            volume = "1 Berkas",
            retensiAktif = retensiAktif,
            retensiInaktif = retensiInaktif,
            keterangan = keterangan,
            sumber = sumber,
            status = "AVAILABLE",
            nasibAkhir = nasibAkhir
        )
    }
}
