package com.example.arsipbpkpad.data.mapper

import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import com.example.arsipbpkpad.domain.model.ArchiveDocument
import com.example.arsipbpkpad.domain.model.DocCondition
import com.example.arsipbpkpad.domain.model.DocCopyType
import com.example.arsipbpkpad.domain.model.DocStatus

fun ArsipEntity.toKeuanganDomain(): ArchiveDocument {
    val parsedType = when {
        deskripsi.contains("SP2D", ignoreCase = true) -> "SP2D"
        deskripsi.contains("SPM", ignoreCase = true) -> "SPM"
        deskripsi.contains("SPP", ignoreCase = true) -> "SPP"
        deskripsi.contains("GU", ignoreCase = true) -> "SP2D"
        deskripsi.contains("LS", ignoreCase = true) -> "SP2D"
        else -> "SP2D"
    }

    val parsedCopyType = if (tingkat.contains("asli", ignoreCase = true)) {
        DocCopyType.ORIGINAL
    } else {
        DocCopyType.COPY
    }

    val parsedStatus = when (status.uppercase()) {
        "AVAILABLE" -> DocStatus.AVAILABLE
        "BORROWED" -> DocStatus.BORROWED
        "DISPOSED" -> DocStatus.DISPOSED
        "UNVERIFIED" -> DocStatus.UNVERIFIED
        else -> DocStatus.AVAILABLE
    }

    val parsedYear = tahun.toIntOrNull() ?: 2020

    return ArchiveDocument(
        id = id,
        boxSessionId = null,
        type = parsedType,
        documentNumber = if (fullKode.isNotBlank()) fullKode else kode,
        copyType = parsedCopyType,
        copyCount = 1,
        classificationCode = kode,
        description = deskripsi,
        nominal = null,
        year = parsedYear,
        condition = DocCondition.GOOD,
        status = parsedStatus,
        metadata = null,
        idStorageLocation = null,
        bundleId = proposalId,
        createdBy = "BPKPAD System",
        verifiedBy = null,
        createdAt = null,
        updatedAt = null,
        deletedAt = if (status == "DISPOSED") disposedAt else null
    )
}
