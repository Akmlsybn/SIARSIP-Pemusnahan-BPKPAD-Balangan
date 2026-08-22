package com.example.arsipbpkpad.utils

object DateUtils {
    /**
     * Formats a date string from YYYY-MM-DD to DD-MM-YYYY for UI display.
     */
    fun formatForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        val parts = dateString.split("-")
        if (parts.size != 3) return dateString
        
        // Check if it's already in DD-MM-YYYY (parts[0].length == 2)
        if (parts[0].length == 2) return dateString
        
        // Convert YYYY-MM-DD to DD-MM-YYYY
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }

    /**
     * Formats an ISO date string to a human-readable format DD-MM-YYYY HH:mm.
     */
    fun formatDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank() || isoString == "-") return "-"
        return try {
            val datePart = isoString.substringBefore("T")
            val timePart = isoString.substringAfter("T").substringBefore(".")
            val formattedDate = formatForDisplay(datePart)
            val formattedTime = timePart.substringBeforeLast(":") // Get HH:mm
            "$formattedDate $formattedTime"
        } catch (e: Exception) {
            isoString
        }
    }
}
