package com.bpkpad.siarsip.feature.arsip.domain.model

data class LogEntry(
    val id: String,
    val time: String,            // e.g. "14:30"
    val dateGroup: String,       // "Hari Ini", "Kemarin", or formatted date
    val sortKey: Long,           // timestamp (preserved from database sorting)
    val categoryName: String,    // "Berkas", "Penilaian", "Disetujui", "Ditolak", "Pemusnahan", "Berita Acara", "Sistem"
    val title: String,
    val description: String,
    val relatedBerkas: String?,
    val person: String,          // directly mapped from database actorId
    val role: String,            // mapped role (e.g. "Operator" or "System")
    val ipAddress: String        // static "Sistem Internal"
)
