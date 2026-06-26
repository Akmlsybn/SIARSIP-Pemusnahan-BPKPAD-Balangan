package com.bpkpad.siarsip.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.security.MessageDigest

class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // Seed admin user
        db.execSQL(
            "INSERT INTO users (username, passwordHash) VALUES ('admin', '${sha256("admin123")}')"
        )
        
        // Seed archives
        val archives = listOf(
            Triple("8ba29a28-98e6-42d4-a15d-3d44dbd9d6f1", "KN.03.01", "00001/SP2D/1.20.11.01/DPPKAD/2016"),
            Triple("7ac96e8e-8a24-4f01-9ba4-500e57fbf072", "KN.03.01", "00002/SP2D/1.20.05.01/DPPKAD/2016"),
            Triple("6b3b55a0-d124-4a49-9df2-bb53d82bc5c3", "KN.03.02", "00003/SP2D/1.20.05.01/DPPKAD/2016"),
            Triple("f849cf24-9bbf-4632-9cb4-8a4dbef6d42e", "KN.04.01", "00004/SP2D/1.07.01.01/DPPKAD/2016"),
            Triple("5f171589-9ebc-443b-be25-78be1a221ad3", "KN.01.02", "00005/KN.01.02/DAU/DPPKAD/2019"),
            Triple("a6b1076b-9c71-4822-ba3b-31d791552cf4", "NK.02.01", "00006/NK.02.01/PERDA/DPPKAD/2018"),
            Triple("cd902a7b-3ef1-4b1f-9da3-872f267a5df2", "NK.05.10", "00007/NK.05.10/SRD/DPPKAD/2017"),
            Triple("28be7a7a-6df7-4d64-9a3b-bf087e2f5b61", "NK.03.05", "00008/NK.03.05/SK/DPPKAD/2018"),
            Triple("b9e02c67-8cf1-45da-982c-738ea9825bc2", "PM.01.03", "00009/PM.01.03/PJM/DPPKAD/2018"),
            Triple("18fa9db9-7ce1-42cb-bdf8-cf18ea205bc1", "PM.02.01", "00010/PM.02.01/DPM/DPPKAD/2019")
        )
        
        val details = listOf(
            listOf("Gaji dan Tunjangan Pegawai Bulan Januari 2016", "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
            listOf("Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD", "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
            listOf("Pembayaran Kekurangan Gaji Bulan Januari 2016", "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
            listOf("Pembayaran Gaji dan Tunjangan PNS Dishubkominfo Kab. Balangan", "2016", "Asli", "2 Berkas", "10 Thn", "Permanen", "Permanen", "Keuangan"),
            listOf("Dana Alokasi Umum Tahun 2019", "2019", "Copy", "1 Berkas", "5 Thn", "8 Thn", "Musnah", "Keuangan"),
            listOf("Peraturan Daerah No. 5 Tahun 2018", "2018", "Asli", "1 Berkas", "10 Thn", "Permanen", "Permanen", "Non-Keuangan"),
            listOf("Surat Dinas Pendidikan Kabupaten Balangan 2017", "2017", "Copy", "1 Berkas", "3 Thn", "5 Thn", "Musnah", "Non-Keuangan"),
            listOf("SK Pegawai Bidang PPKAD Tahun 2018", "2018", "Copy", "1 Berkas", "3 Thn", "5 Thn", "Musnah", "Non-Keuangan"),
            listOf("Arsip Peminjaman Dokumen 2018", "2018", "Copy", "1 Berkas", "5 Thn", "8 Thn", "Musnah", "Peminjaman"),
            listOf("Data Peminjaman Arsip Tahun 2019", "2019", "Copy", "1 Berkas", "2 Thn", "3 Thn", "Musnah", "Peminjaman")
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
            
            db.execSQL("""
                INSERT INTO archives (id, kode, fullKode, deskripsi, tahun, tingkat, volume, retensiAktif, retensiInaktif, keterangan, sumber, status) 
                VALUES ('$uuid', '$kode', '$fullKode', '$deskripsi', '$tahun', '$tingkat', '$volume', '$retAktif', '$retInaktif', '$keterangan', '$sumber', 'AVAILABLE')
            """.trimIndent())
        }
    }

    private fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}