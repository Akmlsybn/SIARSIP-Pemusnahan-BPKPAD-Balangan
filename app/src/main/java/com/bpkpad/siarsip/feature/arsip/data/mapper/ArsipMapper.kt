package com.bpkpad.siarsip.feature.arsip.data.mapper

import com.bpkpad.siarsip.core.database.entity.*
import com.bpkpad.siarsip.feature.arsip.domain.model.*

fun ArsipEntity.toDomain(): Arsip = Arsip(
    id = id,
    kode = kode,
    fullKode = fullKode,
    deskripsi = deskripsi,
    tahun = tahun,
    tingkat = tingkat,
    volume = volume,
    retensiAktif = retensiAktif,
    retensiInaktif = retensiInaktif,
    keterangan = keterangan,
    sumber = sumber,
    status = status,
    proposalId = proposalId,
    beritaAcaraId = beritaAcaraId,
    disposedAt = disposedAt
)

fun Arsip.toEntity(): ArsipEntity = ArsipEntity(
    id = id,
    kode = kode,
    fullKode = fullKode,
    deskripsi = deskripsi,
    tahun = tahun,
    tingkat = tingkat,
    volume = volume,
    retensiAktif = retensiAktif,
    retensiInaktif = retensiInaktif,
    keterangan = keterangan,
    sumber = sumber,
    status = status,
    proposalId = proposalId,
    beritaAcaraId = beritaAcaraId,
    disposedAt = disposedAt
)

fun BerkasUsulMusnahEntity.toDomain(archives: List<Arsip> = emptyList()): BerkasUsulMusnah = BerkasUsulMusnah(
    id = id,
    nomorBerkas = nomorBerkas,
    tanggal = tanggal,
    unitPengolah = unitPengolah,
    sumberModul = sumberModul,
    perihal = perihal,
    status = status,
    createdAt = createdAt,
    archives = archives
)

fun BerkasUsulMusnah.toEntity(): BerkasUsulMusnahEntity = BerkasUsulMusnahEntity(
    id = id,
    nomorBerkas = nomorBerkas,
    tanggal = tanggal,
    unitPengolah = unitPengolah,
    sumberModul = sumberModul,
    perihal = perihal,
    status = status,
    createdAt = createdAt
)

fun BeritaAcaraEntity.toDomain(signatories: List<Penandatangan> = emptyList(), archives: List<Arsip> = emptyList()): BeritaAcara = BeritaAcara(
    id = id,
    nomorBa = nomorBa,
    tanggalEksekusi = tanggalEksekusi,
    penanggungJawab = penanggungJawab,
    saksi1 = saksi1,
    saksi2 = saksi2,
    keterangan = keterangan,
    createdAt = createdAt,
    signatories = signatories,
    archives = archives
)

fun BeritaAcara.toEntity(): BeritaAcaraEntity = BeritaAcaraEntity(
    id = id,
    nomorBa = nomorBa,
    tanggalEksekusi = tanggalEksekusi,
    penanggungJawab = penanggungJawab,
    saksi1 = saksi1,
    saksi2 = saksi2,
    keterangan = keterangan,
    createdAt = createdAt
)

fun PenandatanganEntity.toDomain(): Penandatangan = Penandatangan(
    id = id,
    beritaAcaraId = beritaAcaraId,
    nama = nama,
    jabatan = jabatan,
    role = role,
    urutan = urutan
)

fun Penandatangan.toEntity(): PenandatanganEntity = PenandatanganEntity(
    id = id,
    beritaAcaraId = beritaAcaraId,
    nama = nama,
    jabatan = jabatan,
    role = role,
    urutan = urutan
)

fun AuditLogEntity.toDomain(): AuditLog = AuditLog(
    id = id,
    action = action,
    actorId = actorId,
    archiveId = archiveId,
    proposalId = proposalId,
    beritaAcaraId = beritaAcaraId,
    previousStatus = previousStatus,
    newStatus = newStatus,
    notes = notes,
    timestamp = timestamp
)

fun AuditLog.toEntity(): AuditLogEntity = AuditLogEntity(
    id = id,
    action = action,
    actorId = actorId,
    archiveId = archiveId,
    proposalId = proposalId,
    beritaAcaraId = beritaAcaraId,
    previousStatus = previousStatus,
    newStatus = newStatus,
    notes = notes,
    timestamp = timestamp
)
