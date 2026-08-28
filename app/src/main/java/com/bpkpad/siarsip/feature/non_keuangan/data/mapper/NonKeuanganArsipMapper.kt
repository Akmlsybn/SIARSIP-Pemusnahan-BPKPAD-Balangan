package com.bpkpad.siarsip.feature.non_keuangan.data.mapper

import com.bpkpad.arsip.domain.model.ArchiveDocument
import com.bpkpad.siarsip.core.database.entity.ArsipEntity

fun ArsipEntity.toNonKeuanganDomain(): ArchiveDocument {
    val parsedType = when {
        deskripsi.contains("PERBUP", ignoreCase = true) -> "PERBUP"
        deskripsi.contains("PERDA", ignoreCase = true) -> "PERDA"
        deskripsi.contains("Surat Tugas", ignoreCase = true) -> "SURAT"
        deskripsi.contains("SK", ignoreCase = true) -> "KEPUTUSAN_BUPATI"
        else -> "DOKUMEN"
    }

    val displayTitle = if (fullKode.isNotBlank()) {
        "$fullKode - $deskripsi"
    } else {
        deskripsi
    }

    val yearInt = tahun.toIntOrNull() ?: 2024

    return ArchiveDocument(
        id = id,
        title = displayTitle,
        type = parsedType,
        date = System.currentTimeMillis(),
        description = deskripsi,
        boxId = proposalId ?: "BOX-NK-$yearInt",
        locationId = sumber,
        imageUrl = null
    )
}
