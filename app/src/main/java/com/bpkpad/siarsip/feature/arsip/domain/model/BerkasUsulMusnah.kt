package com.bpkpad.siarsip.feature.arsip.domain.model

data class BerkasUsulMusnah(
    val id: String,
    val nomorBerkas: String,
    val tanggal: String,
    val unitPengolah: String,
    val sumberModul: String,
    val perihal: String,
    val status: String,
    val createdAt: Long,
    val archives: List<Arsip> = emptyList(),
    val suratPertimbanganNomor: String? = null,
    val suratPertimbanganPerihal: String? = null,
    val jenisPersetujuanAkhir: String? = null,
    val nomorPersetujuanAkhir: String? = null,
    val perihalPersetujuanAkhir: String? = null
)
