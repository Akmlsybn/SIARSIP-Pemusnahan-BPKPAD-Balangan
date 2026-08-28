package com.bpkpad.peminjaman.peminjaman.data.mapper

import com.bpkpad.peminjaman.peminjaman.domain.model.MasterDokumen
import com.bpkpad.peminjaman.peminjaman.domain.model.enums.DokumenStatus
import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import kotlin.math.abs

fun ArsipEntity.toPeminjamanDomain(): MasterDokumen {
    val parsedStatus = if (status.uppercase() == "BORROWED") {
        DokumenStatus.DIPINJAM
    } else {
        DokumenStatus.TERSEDIA
    }

    val intId = abs(id.hashCode())

    return MasterDokumen(
        id = intId,
        nomorDokumen = if (fullKode.isNotBlank()) fullKode else kode,
        perihal = deskripsi,
        nominal = 0.0,
        tahun = if (tahun.isNotBlank()) tahun else "2024",
        jenisDokumen = sumber,
        status = parsedStatus,
        lokasiRak = "RAK-01",
        lokasiBox = "BOX-01"
    )
}
