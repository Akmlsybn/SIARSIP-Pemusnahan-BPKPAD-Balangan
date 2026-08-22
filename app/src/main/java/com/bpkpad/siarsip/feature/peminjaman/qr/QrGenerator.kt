package com.bpkpad.peminjaman.qr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

object QrGenerator {

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)

            val paint = Paint().apply {
                color = Color.BLACK
                isAntiAlias = true
            }

            val margin = size * 0.1f
            val qrSize = size - (margin * 2)

            // Draw outer border
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 12f
            canvas.drawRect(margin, margin, margin + qrSize, margin + qrSize, paint)

            // Draw inner positioning squares
            paint.style = Paint.Style.FILL
            canvas.drawRect(margin + 24f, margin + 24f, margin + 80f, margin + 80f, paint)
            canvas.drawRect(margin + qrSize - 80f, margin + 24f, margin + qrSize - 24f, margin + 80f, paint)
            canvas.drawRect(margin + 24f, margin + qrSize - 80f, margin + 80f, margin + qrSize - 24f, paint)

            bitmap
        } catch (e: Exception) {
            android.util.Log.e("QR_GENERATOR", "Failed to generate QR: ${e.message}")
            null
        }
    }
}
