package com.bpkpad.siarsip.core.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")

    object DaftarArsip : Screen("daftar_arsip")
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
    object Pengaturan : Screen("pengaturan")
}