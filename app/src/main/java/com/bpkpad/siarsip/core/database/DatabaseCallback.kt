package com.bpkpad.siarsip.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bpkpad.siarsip.core.database.entity.UserEntity
import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class DatabaseCallback(
    private val databaseProvider: () -> AppDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = databaseProvider()
            val userDao = database.userDao()
            if (userDao.countUsers() == 0) {
                userDao.insertUser(
                    UserEntity(
                        username = "admin",
                        passwordHash = sha256("admin123")
                    )
                )
            }
            
            val arsipDao = database.arsipDao()
            if (arsipDao.countArchives() == 0) {
                arsipDao.insertArchives(
                    listOf(
                        ArsipEntity(
                            id = "8ba29a28-98e6-42d4-a15d-3d44dbd9d6f1",
                            kode = "KN.03.01",
                            fullKode = "00001/SP2D/1.20.11.01/DPPKAD/2016",
                            deskripsi = "Gaji dan Tunjangan Pegawai Bulan Januari 2016",
                            tahun = "2016",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "2 Thn",
                            retensiInaktif = "8 Thn",
                            keterangan = "Musnah",
                            sumber = "Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "7ac96e8e-8a24-4f01-9ba4-500e57fbf072",
                            kode = "KN.03.01",
                            fullKode = "00002/SP2D/1.20.05.01/DPPKAD/2016",
                            deskripsi = "Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD",
                            tahun = "2016",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "2 Thn",
                            retensiInaktif = "8 Thn",
                            keterangan = "Musnah",
                            sumber = "Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "6b3b55a0-d124-4a49-9df2-bb53d82bc5c3",
                            kode = "KN.03.02",
                            fullKode = "00003/SP2D/1.20.05.01/DPPKAD/2016",
                            deskripsi = "Pembayaran Kekurangan Gaji Bulan Januari 2016",
                            tahun = "2016",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "2 Thn",
                            retensiInaktif = "8 Thn",
                            keterangan = "Musnah",
                            sumber = "Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "f849cf24-9bbf-4632-9cb4-8a4dbef6d42e",
                            kode = "KN.04.01",
                            fullKode = "00004/SP2D/1.07.01.01/DPPKAD/2016",
                            deskripsi = "Pembayaran Gaji dan Tunjangan PNS Dishubkominfo Kab. Balangan",
                            tahun = "2016",
                            tingkat = "Asli",
                            volume = "2 Berkas",
                            retensiAktif = "10 Thn",
                            retensiInaktif = "Permanen",
                            keterangan = "Permanen",
                            sumber = "Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "5f171589-9ebc-443b-be25-78be1a221ad3",
                            kode = "KN.01.02",
                            fullKode = "00005/KN.01.02/DAU/DPPKAD/2019",
                            deskripsi = "Dana Alokasi Umum Tahun 2019",
                            tahun = "2019",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "5 Thn",
                            retensiInaktif = "8 Thn",
                            keterangan = "Musnah",
                            sumber = "Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "a6b1076b-9c71-4822-ba3b-31d791552cf4",
                            kode = "NK.02.01",
                            fullKode = "00006/NK.02.01/PERDA/DPPKAD/2018",
                            deskripsi = "Peraturan Daerah No. 5 Tahun 2018",
                            tahun = "2018",
                            tingkat = "Asli",
                            volume = "1 Berkas",
                            retensiAktif = "10 Thn",
                            retensiInaktif = "Permanen",
                            keterangan = "Permanen",
                            sumber = "Non-Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "cd902a7b-3ef1-4b1f-9da3-872f267a5df2",
                            kode = "NK.05.10",
                            fullKode = "00007/NK.05.10/SRD/DPPKAD/2017",
                            deskripsi = "Surat Dinas Pendidikan Kabupaten Balangan 2017",
                            tahun = "2017",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "3 Thn",
                            retensiInaktif = "5 Thn",
                            keterangan = "Musnah",
                            sumber = "Non-Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "28be7a7a-6df7-4d64-9a3b-bf087e2f5b61",
                            kode = "NK.03.05",
                            fullKode = "00008/NK.03.05/SK/DPPKAD/2018",
                            deskripsi = "SK Pegawai Bidang PPKAD Tahun 2018",
                            tahun = "2018",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "3 Thn",
                            retensiInaktif = "5 Thn",
                            keterangan = "Musnah",
                            sumber = "Non-Keuangan",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "b9e02c67-8cf1-45da-982c-738ea9825bc2",
                            kode = "PM.01.03",
                            fullKode = "00009/PM.01.03/PJM/DPPKAD/2018",
                            deskripsi = "Arsip Peminjaman Dokumen 2018",
                            tahun = "2018",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "5 Thn",
                            retensiInaktif = "8 Thn",
                            keterangan = "Musnah",
                            sumber = "Peminjaman",
                            status = "AVAILABLE"
                        ),
                        ArsipEntity(
                            id = "18fa9db9-7ce1-42cb-bdf8-cf18ea205bc1",
                            kode = "PM.02.01",
                            fullKode = "00010/PM.02.01/DPM/DPPKAD/2019",
                            deskripsi = "Data Peminjaman Arsip Tahun 2019",
                            tahun = "2019",
                            tingkat = "Copy",
                            volume = "1 Berkas",
                            retensiAktif = "2 Thn",
                            retensiInaktif = "3 Thn",
                            keterangan = "Musnah",
                            sumber = "Peminjaman",
                            status = "AVAILABLE"
                        )
                    )
                )
            }
        }
    }

    private fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}