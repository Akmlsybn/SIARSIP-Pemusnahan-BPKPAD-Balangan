package com.bpkpad.siarsip.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    
    // ── Portal Hub ──────────────────────────────────────────────────────────
    object ModuleSelection : Screen("module_selection")

    // ── Modul Pemusnahan (Main App) ─────────────────────────────────────────
    object Dashboard : Screen("dashboard")
    object DaftarArsip : Screen("daftar_arsip?beritaAcaraId={beritaAcaraId}") {
        fun createRoute(beritaAcaraId: String? = null) = 
            if (beritaAcaraId != null) "daftar_arsip?beritaAcaraId=$beritaAcaraId" else "daftar_arsip"
    }
    object DaftarUsulMusnah : Screen("daftar_usul_musnah")
    object BuatBerkasUsulMusnah : Screen("buat_berkas_usul_musnah")
    object DetailBerkasUsulMusnah : Screen("detail_berkas_usul_musnah/{berkasNomor}") {
        fun createRoute(berkasNomor: String) = "detail_berkas_usul_musnah/$berkasNomor"
    }
    object StatusTracking : Screen("status_tracking")
    object BeritaAcara : Screen("berita_acara")
    object DetailBeritaAcara : Screen("detail_berita_acara/{baId}") {
        fun createRoute(baId: String) = "detail_berita_acara/$baId"
    }
    object LogRiwayat : Screen("log_riwayat")
    object Profil : Screen("profil")

    // ── Modul Keuangan ──────────────────────────────────────────────────────
    object KeuanganHome : Screen("keuangan_home")
    object KeuanganArchiveList : Screen("keuangan_archive_list?year={year}") {
        fun createRoute(year: Int? = null) = if (year != null) "keuangan_archive_list?year=$year" else "keuangan_archive_list"
    }
    object KeuanganArchiveDetail : Screen("keuangan_archive_detail/{archiveId}") {
        fun createRoute(archiveId: String) = "keuangan_archive_detail/$archiveId"
    }
    object KeuanganRapidInput : Screen("keuangan_rapid_input?sessionId={sessionId}") {
        fun createRoute(sessionId: String? = null) = if (sessionId != null) "keuangan_rapid_input?sessionId=$sessionId" else "keuangan_rapid_input"
    }
    object KeuanganScan : Screen("keuangan_scan")
    object KeuanganBoxManagement : Screen("keuangan_box_management")
    object KeuanganStagingBoxList : Screen("keuangan_staging_box_list")
    object KeuanganAnalytics : Screen("keuangan_analytics")

    // ── Modul Non-Keuangan ──────────────────────────────────────────────────
    object NonKeuanganHome : Screen("non_keuangan_home")
    object NonKeuanganDetail : Screen("non_keuangan_detail/{documentId}") {
        fun createRoute(documentId: String) = "non_keuangan_detail/$documentId"
    }
    object NonKeuanganStaging : Screen("non_keuangan_staging")
    object NonKeuanganEditStaging : Screen("non_keuangan_edit_staging/{stagingId}") {
        fun createRoute(stagingId: String) = "non_keuangan_edit_staging/$stagingId"
    }
    object NonKeuanganInput : Screen("non_keuangan_input")
    object NonKeuanganScan : Screen("non_keuangan_scan")

    // ── Modul Peminjaman ────────────────────────────────────────────────────
    object PeminjamanDashboard : Screen("peminjaman_dashboard")
    object PeminjamanDashboardKasubag : Screen("peminjaman_dashboard_kasubag")
    object PeminjamanForm : Screen("peminjaman_form")
    object PeminjamanApproval : Screen("peminjaman_approval")
    object PeminjamanScanQr : Screen("peminjaman_scan_qr")
    object PeminjamanDetail : Screen("peminjaman_detail/{transaksiId}") {
        fun createRoute(id: Int) = "peminjaman_detail/$id"
    }
    object PeminjamanRiwayat : Screen("peminjaman_riwayat?status={status}") {
        fun createRoute(status: String? = null) = if (status != null) "peminjaman_riwayat?status=$status" else "peminjaman_riwayat"
    }
    object PeminjamanInstansi : Screen("peminjaman_instansi")
    object PeminjamanDokumen : Screen("peminjaman_dokumen")
    object PeminjamanLaporan : Screen("peminjaman_laporan")
}