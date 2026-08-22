package com.bpkpad.siarsip.core.utils

object PemusnahanInputRules {

    // Menyaring hanya tag HTML/Script berbahaya (< > " ' ;), TETAP Mengizinkan garis miring (/) dan backslash (\) untuk nomor surat resmi
    private val DANGEROUS_HTML_REGEX = Regex("[<>\"';]")
    private val NAME_WITH_TITLES_REGEX = Regex("^[a-zA-Z\\s.,'-]+$")

    fun sanitize(input: String): String {
        return input.replace(DANGEROUS_HTML_REGEX, "").trim()
    }

    fun validatePerihal(perihal: String): String? {
        val clean = sanitize(perihal)
        if (clean.isBlank()) return "Perihal usulan musnah tidak boleh kosong"
        if (clean.length < 5) return "Perihal usulan minimal 5 karakter"
        if (clean.length > 250) return "Perihal usulan maksimal 250 karakter"
        return null
    }

    fun validateNomorBa(nomorBa: String): String? {
        val clean = sanitize(nomorBa)
        if (clean.isBlank()) return "Nomor Berita Acara tidak boleh kosong"
        if (clean.length < 5) return "Nomor Berita Acara minimal 5 karakter"
        if (clean.length > 100) return "Nomor Berita Acara maksimal 100 karakter"
        return null
    }

    fun validateNamaPejabat(nama: String, fieldLabel: String = "Nama"): String? {
        val clean = sanitize(nama)
        if (clean.isBlank()) return "$fieldLabel tidak boleh kosong"
        if (clean.length < 3) return "$fieldLabel minimal 3 karakter"
        if (!NAME_WITH_TITLES_REGEX.matches(clean)) {
            return "$fieldLabel hanya boleh berisi huruf, spasi, titik, koma, dan simbol gelar"
        }
        return null
    }

    fun validateKeterangan(keterangan: String): String? {
        val clean = sanitize(keterangan)
        if (clean.length > 500) return "Keterangan maksimal 500 karakter"
        return null
    }
}
