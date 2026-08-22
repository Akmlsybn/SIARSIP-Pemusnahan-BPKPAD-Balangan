package com.example.arsipbpkpad.domain.model

object DocumentTypeDefaults {
    const val SP2D = "SP2D"
    const val SPM = "SPM"
    const val SPP = "SPP"
    const val SPJ = "SPJ"

    val systemTypes = listOf(SP2D, SPM, SPP, SPJ)

    /**
     * Normalizes document type by trimming, replacing multiple spaces with one, 
     * and converting to uppercase.
     */
    fun normalizeDocumentType(input: String): String {
        return input.trim()
            .replace(Regex("\\s+"), " ")
            .uppercase()
    }
}
