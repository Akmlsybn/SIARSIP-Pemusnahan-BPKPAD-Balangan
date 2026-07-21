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
    disposedAt = disposedAt,
    nasibAkhir = nasibAkhir
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
    disposedAt = disposedAt,
    nasibAkhir = nasibAkhir
)

fun BerkasUsulMusnahEntity.toDomain(archives: List<Arsip> = emptyList()): BerkasUsulMusnah = BerkasUsulMusnah(
    id = id,
    nomorBerkas = nomorBerkas,
    tanggal = tanggal,
    unitPengolah = unitPengolah,
    sumberModul = if (archives.isNotEmpty()) {
        archives.map { it.sumber }.distinct().sorted().joinToString(", ")
    } else {
        sumberModul
    },
    perihal = perihal,
    status = status,
    createdAt = createdAt,
    archives = archives,
    suratPertimbanganNomor = suratPertimbanganNomor,
    suratPertimbanganPerihal = suratPertimbanganPerihal,
    jenisPersetujuanAkhir = jenisPersetujuanAkhir,
    nomorPersetujuanAkhir = nomorPersetujuanAkhir,
    perihalPersetujuanAkhir = perihalPersetujuanAkhir
)

fun BerkasUsulMusnah.toEntity(): BerkasUsulMusnahEntity = BerkasUsulMusnahEntity(
    id = id,
    nomorBerkas = nomorBerkas,
    tanggal = tanggal,
    unitPengolah = unitPengolah,
    sumberModul = sumberModul,
    perihal = perihal,
    status = status,
    createdAt = createdAt,
    suratPertimbanganNomor = suratPertimbanganNomor,
    suratPertimbanganPerihal = suratPertimbanganPerihal,
    jenisPersetujuanAkhir = jenisPersetujuanAkhir,
    nomorPersetujuanAkhir = nomorPersetujuanAkhir,
    perihalPersetujuanAkhir = perihalPersetujuanAkhir
)

fun BeritaAcaraEntity.toDomain(signatories: List<Penandatangan> = emptyList(), archives: List<Arsip> = emptyList()): BeritaAcara = BeritaAcara(
    id = id,
    nomorBa = nomorBa,
    tanggalEksekusi = tanggalEksekusi,
    penanggungJawab = penanggungJawab,
    saksi1 = saksi1,
    saksi2 = saksi2,
    keterangan = keterangan,
    metode = metode,
    createdAt = createdAt,
    signatories = signatories,
    archives = archives
)

fun BeritaAcaraWithRelations.toDomain(): BeritaAcara = beritaAcara.toDomain(
    signatories = signatories.sortedBy { it.urutan }.map { it.toDomain() },
    archives = archives.map { it.toDomain() }
)


fun BeritaAcara.toEntity(): BeritaAcaraEntity = BeritaAcaraEntity(
    id = id,
    nomorBa = nomorBa,
    tanggalEksekusi = tanggalEksekusi,
    penanggungJawab = penanggungJawab,
    saksi1 = saksi1,
    saksi2 = saksi2,
    keterangan = keterangan,
    metode = metode,
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
