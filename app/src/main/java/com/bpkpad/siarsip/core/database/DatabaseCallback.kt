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
        val now = System.currentTimeMillis()

        // 1. Seed admin user
        safeExec(db, "INSERT OR IGNORE INTO users (username, passwordHash) VALUES ('admin', '${sha256("admin123")}')")
        
        // 2. Seed initial Berkas Usul Musnah (Proposals)
        safeExec(db, """
            INSERT OR IGNORE INTO proposals (id, nomorBerkas, tanggal, unitPengolah, sumberModul, perihal, status, createdAt, suratPertimbanganNomor, suratPertimbanganPerihal, jenisPersetujuanAkhir, nomorPersetujuanAkhir, perihalPersetujuanAkhir)
            VALUES ('init-proposal-disposed-uuid', '001/UM/BPKPAD/2025', '15/05/2025', 'Sekretariat', 'Keuangan', 'Usul Musnah Arsip Keuangan Pertanggungjawaban 2013-2014', 'DISPOSED', ${now - 3600000 * 24}, '100/PANITIA-PENILAI/2025', 'Pertimbangan Pemusnahan Arsip Keuangan', 'Bupati Balangan', '045.2/330/BPKPAD/2025', 'Persetujuan Pemusnahan Arsip Keuangan')
        """.trimIndent())

        safeExec(db, """
            INSERT OR IGNORE INTO proposals (id, nomorBerkas, tanggal, unitPengolah, sumberModul, perihal, status, createdAt, suratPertimbanganNomor, suratPertimbanganPerihal, jenisPersetujuanAkhir, nomorPersetujuanAkhir, perihalPersetujuanAkhir)
            VALUES ('proposal-approved-2026-uuid', '002/UM/BPKPAD/2026', '20/06/2026', 'Bidang Kearsipan', 'Non-Keuangan', 'Usul Musnah Dokumen Rencana Kerja Anggaran', 'APPROVED', ${now - 3600000 * 5}, '101/PANITIA-PENILAI/2026', 'Pertimbangan Pemusnahan Dokumen Kerja', 'Bupati Balangan', '045.2/410/BPKPAD/2026', 'Persetujuan Pemusnahan Dokumen RKA')
        """.trimIndent())

        safeExec(db, """
            INSERT OR IGNORE INTO proposals (id, nomorBerkas, tanggal, unitPengolah, sumberModul, perihal, status, createdAt, suratPertimbanganNomor, suratPertimbanganPerihal, jenisPersetujuanAkhir, nomorPersetujuanAkhir, perihalPersetujuanAkhir)
            VALUES ('proposal-approved-2025-uuid', '003/UM/BPKPAD/2025', '12/10/2025', 'Bidang Aset', 'Peminjaman', 'Usul Musnah Berkas Peminjaman Sarpras', 'APPROVED', ${now - 3600000 * 8}, '98/PANITIA-PENILAI/2025', 'Pertimbangan Pemusnahan Dokumen Peminjaman', 'ANRI', 'B-ANRI/120/X/2025', 'Persetujuan Pemusnahan Arsip Peminjaman')
        """.trimIndent())

        safeExec(db, """
            INSERT OR IGNORE INTO proposals (id, nomorBerkas, tanggal, unitPengolah, sumberModul, perihal, status, createdAt)
            VALUES ('proposal-proposed-2024-uuid', '004/UM/BPKPAD/2024', '05/04/2024', 'Bidang Keuangan', 'Keuangan', 'Usul Musnah SP2D Perjalanan Dinas', 'PROPOSED', ${now - 3600000 * 12})
        """.trimIndent())

        // 3. Seed Main Archives (Pemusnahan)
        val archives = listOf(
            Triple("8ba29a28-98e6-42d4-a15d-3d44dbd9d6f1", "900.03.01", "00001/SP2D/1.20.11.01/DPPKAD/2016"),
            Triple("7ac96e8e-8a24-4f01-9ba4-500e57fbf072", "900.03.01", "00002/SP2D/1.20.05.01/DPPKAD/2016"),
            Triple("cd902a7b-3ef1-4b1f-9da3-872f267a5df2", "180.05.10", "00007/NK.05.10/SRD/DPPKAD/2017"),
            Triple("a6b1076b-9c71-4822-ba3b-31d791552cf4", "061.02.01", "00006/NK.02.01/PERDA/DPPKAD/2018"),
            Triple("5f171589-9ebc-443b-be25-78be1a221ad3", "900.01.02", "00005/KN.01.02/DAU/DPPKAD/2019"),
            Triple("a1ba9a28-98e6-42d4-a15d-3d44dbd9d6f0", "900.03.01", "00011/SP2D/DPPKAD/2020"),
            Triple("a2ac96e8-8a24-4f01-9ba4-500e57fbf070", "900.03.02", "00012/SP2D/DPPKAD/2021"),
            Triple("a3b355a0-d124-4a49-9df2-bb53d82bc5c0", "900.04.02", "00013/SP2D/DPPKAD/2022"),
            Triple("a449cf24-9bbf-4632-9cb4-8a4dbef6d420", "900.01.03", "00014/KN.01.03/DPPKAD/2023"),
            Triple("a5171589-9ebc-443b-be25-78be1a221ad0", "061.02.02", "00015/NK.02.02/DPPKAD/2024"),
            Triple("b0fa9db0-7ce1-42cb-bdf8-cf18ea205bc0", "900.03.01", "00020/SP2D/DPPKAD/2025"),
            Triple("a7d902a8-3ef1-4b1f-9da3-872f267a5df0", "800.03.06", "00017/NK.03.06/DPPKAD/2026")
        )
        
        val details = listOf(
            listOf("Gaji dan Tunjangan Pegawai Bulan Januari 2016", "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD", "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Surat Dinas Pendidikan Kabupaten Balangan 2017", "2017", "Copy", "1 Berkas", "2 Thn", "3 Thn", "Musnah", "Non-Keuangan", "AVAILABLE", ""),
            listOf("Peraturan Daerah No. 5 Tahun 2018", "2018", "Asli", "1 Berkas", "2 Thn", "3 Thn", "Permanen", "Non-Keuangan", "AVAILABLE", ""),
            listOf("Dana Alokasi Umum Tahun 2019", "2019", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Laporan Keuangan Semester I Tahun 2020", "2020", "Copy", "1 Box", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("SP2D Belanja Barang dan Jasa 2021", "2021", "Asli", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Kekurangan Gaji Pegawai BPKPAD Semester II 2022", "2022", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Laporan Pendapatan Asli Daerah (PAD) 2023", "2023", "Asli", "1 Box", "3 Thn", "5 Thn", "Musnah", "Keuangan", "AVAILABLE", ""),
            listOf("Dokumen Analisis Jabatan Pegawai BPKPAD 2024", "2024", "Copy", "1 Berkas", "2 Thn", "3 Thn", "Musnah", "Non-Keuangan", "AVAILABLE", ""),
            listOf("SP2D Perjalanan Dinas Daerah BPKPAD 2025", "2025", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan", "APPROVED", "proposal-approved-2025-uuid"),
            listOf("Dokumen Rencana Kerja Anggaran (RKA) BPKPAD 2026", "2026", "Asli", "2 Berkas", "2 Thn", "8 Thn", "Musnah", "Non-Keuangan", "APPROVED", "proposal-approved-2026-uuid")
        )
        
        for (i in archives.indices) {
            val (uuid, kode, fullKode) = archives[i]
            val detail = details[i]
            val deskripsi = detail[0]
            val tahun = detail[1]
            val tingkat = detail[2]
            val volume = detail[3]
            val retAktif = detail[4]
            val retInaktif = detail[5]
            val keterangan = detail[6]
            val sumber = detail[7]
            val status = detail[8]
            val proposalId = detail[9]
            val nasibAkhir = if (keterangan == "Permanen") "PERMANEN" else "MUSNAH"
            
            val propVal = if (proposalId.isBlank()) "NULL" else "'$proposalId'"
            
            safeExec(db, """
                INSERT OR IGNORE INTO archives (id, kode, fullKode, deskripsi, tahun, tingkat, volume, retensiAktif, retensiInaktif, keterangan, sumber, status, proposalId, nasibAkhir) 
                VALUES ('$uuid', '$kode', '$fullKode', '$deskripsi', '$tahun', '$tingkat', '$volume', '$retAktif', '$retInaktif', '$keterangan', '$sumber', '$status', $propVal, '$nasibAkhir')
            """.trimIndent())
        }

        // 4. Seed Keuangan Archives (Tabel `keuangan_archives`)
        val keuanganArchives = listOf(
            Triple("kq-1", "00001/SP2D/BPKPAD/2026", "SP2D Gaji & Tunjangan Januari 2026"),
            Triple("kq-2", "00002/SPM/BPKPAD/2026", "SPM Pengadaan Sarpras Kantor"),
            Triple("kq-3", "00003/SPJ/BPKPAD/2025", "SPJ Perjalanan Dinas Sosial 2025"),
            Triple("kq-4", "00004/SP2D/BPKPAD/2025", "SP2D Belanja Modal Gedung 2025"),
            Triple("kq-5", "00005/SPM/BPKPAD/2024", "SPM Honorarium Panitia Penilai 2024")
        )
        for (item in keuanganArchives) {
            val (id, docNum, desc) = item
            val docType = if (docNum.contains("SP2D")) "SP2D" else if (docNum.contains("SPM")) "SPM" else "SPJ"
            val year = if (docNum.contains("2026")) 2026 else if (docNum.contains("2025")) 2025 else 2024
            safeExec(db, """
                INSERT OR IGNORE INTO keuangan_archives (id, type, copyType, copyCount, documentNumber, classificationCode, description, nominal, year, condition, status, syncStatus)
                VALUES ('$id', '$docType', 'ORIGINAL', 1, '$docNum', '900.1.3.1', '$desc', 15000000.0, $year, 'GOOD', 'AVAILABLE', 'SYNCED')
            """.trimIndent())
        }

        // 5. Seed Berita Acara & Audit Logs
        safeExec(db, """
            INSERT OR IGNORE INTO audit_logs (id, action, actorId, notes, timestamp)
            VALUES ('init-db-system-uuid', 'INIT_DB', 'Sistem', 'Inisialisasi basis data sistem pengarsipan BPKPAD Balangan.', ${now - 3600000 * 2})
        """.trimIndent())

        safeExec(db, """
            INSERT OR IGNORE INTO berita_acara (id, nomorBa, tanggalEksekusi, penanggungJawab, saksi1, saksi2, keterangan, metode, createdAt, fotoDokumentasiUri)
            VALUES ('init-ba-uuid-11111', 'BA.03.01/001/BPKPAD/2025', '15 Mei 2025', 'Ahmad Fauzi', 'H. Supian, S.Sos', 'M. Rasyid, A.Md', 'Pemusnahan fisik arsip keuangan kurun waktu 2013-2014.', 'Pencacahan', ${now - 3600000 * 5}, NULL)
        """.trimIndent())
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