package com.bpkpad.siarsip.core.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.bpkpad.siarsip.R
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import java.io.OutputStream

object PdfExportManager {

    fun exportToPdf(context: Context, ba: BeritaAcaraItem, outputStream: OutputStream) {
        val pdfDocument = PdfDocument()

        // ── Halaman 1: Kop Surat, Metadata & Dasar Hukum ──────────────
        val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas

        drawPage1(context, canvas1, ba)
        pdfDocument.finishPage(page1)

        // ── Halaman 2: Tanda Tangan Saksi & Penanggung Jawab ──────────
        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        val canvas2 = page2.canvas

        drawPage2(canvas2, ba)
        pdfDocument.finishPage(page2)

        // ── Halaman 3: Lampiran Foto Dokumentasi (Jika Ada) ───────────
        if (!ba.fotoDokumentasiUri.isNullOrBlank()) {
            try {
                val pageInfo3 = PdfDocument.PageInfo.Builder(595, 842, 3).create()
                val page3 = pdfDocument.startPage(pageInfo3)
                drawPage3(context, page3.canvas, ba)
                pdfDocument.finishPage(page3)
            } catch (e: Exception) {
                // Abaikan jika halaman 3 gagal digambar
            }
        }

        // Tulis dokumen ke stream
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
    }

    private fun drawPage1(context: Context, canvas: Canvas, ba: BeritaAcaraItem) {
        val paint = Paint()
        val textPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }

        // 1. Gambar Logo Instansi (Mipmap ic_launcher sebagai placeholder)
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            if (logoBitmap != null) {
                val logoSize = 48f
                val logoX = 54f
                val logoY = 54f
                val srcRect = Rect(0, 0, logoBitmap.width, logoBitmap.height)
                val destRect = RectF(logoX, logoY, logoX + logoSize, logoY + logoSize)
                canvas.drawBitmap(logoBitmap, srcRect, destRect, paint)
            }
        } catch (e: Exception) {
            // Abaikan jika logo gagal dimuat
        }

        // 2. Kop Surat Teks
        paint.color = Color.BLACK
        paint.isAntiAlias = true

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("PEMERINTAH KABUPATEN BALANGAN", 114f, 68f, paint)

        paint.textSize = 10f
        canvas.drawText("BADAN PENGELOLA KEUANGAN, PENDAPATAN", 114f, 82f, paint)
        canvas.drawText("DAN ASET DAERAH", 114f, 94f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 8f
        canvas.drawText("Alamat: Jl. A. Yani No. 1 Paringin, Kode Pos 71612", 114f, 106f, paint)

        // Garis Pembatas Kop Surat
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawLine(54f, 116f, 541f, 116f, paint)
        paint.strokeWidth = 0.5f
        canvas.drawLine(54f, 120f, 541f, 120f, paint)

        // 3. Judul Berita Acara
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        drawCenteredText(canvas, "BERITA ACARA PEMUSNAHAN ARSIP", paint, 297.5f, 150f)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        drawCenteredText(canvas, "Nomor: ${ba.nomor}", paint, 297.5f, 166f)

        // 4. Paragraf Pembukaan
        var currentY = 195f
        val printableWidth = 487 // 595 - 54 - 54

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 10f

        val openingText = "Pada hari ini, tanggal ${ba.tanggal}, bertempat di ${ba.lokasi}, " +
                "kami yang bertanda tangan di bawah ini telah melaksanakan pemusnahan arsip milik " +
                "${ba.sumber} Kabupaten Balangan dengan metode ${ba.metode}."
        
        currentY += drawWrappedText(canvas, openingText, textPaint, printableWidth, 54f, currentY) + 12f

        // 5. Paragraf Jumlah Arsip
        val archiveSummaryText = "Arsip yang dimusnahkan terdiri dari berkas usul musnah dengan rincian sebagai berikut:"
        currentY += drawWrappedText(canvas, archiveSummaryText, textPaint, printableWidth, 54f, currentY) + 8f

        // Poin Detail
        canvas.drawText("•  Jumlah Arsip :  ${ba.jumlahArsip} Berkas", 72f, currentY, paint)
        currentY += 15f
        canvas.drawText("•  Kurun Waktu :  Tahun ${ba.tahun}", 72f, currentY, paint)
        currentY += 24f

        // 6. Dasar Hukum
        val dasarHukumText = "Pemusnahan ini dilaksanakan dengan dasar hukum dan persetujuan sebagai berikut:"
        currentY += drawWrappedText(canvas, dasarHukumText, textPaint, printableWidth, 54f, currentY) + 8f

        // Poin 1
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. Surat Pertimbangan Panitia Penilai Arsip:", 72f, currentY, paint)
        currentY += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Nomor   :  ${ba.suratPertimbanganNomor ?: "-"}", 86f, currentY, paint)
        currentY += 13f
        canvas.drawText("Perihal   :  ${ba.suratPertimbanganPerihal ?: "-"}", 86f, currentY, paint)
        currentY += 20f

        // Poin 2
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. Surat Persetujuan Akhir ${ba.jenisPersetujuanAkhir ?: "Bupati/ANRI"}:", 72f, currentY, paint)
        currentY += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Nomor   :  ${ba.nomorPersetujuanAkhir ?: "-"}", 86f, currentY, paint)
        currentY += 13f
        canvas.drawText("Perihal   :  ${ba.perihalPersetujuanAkhir ?: "-"}", 86f, currentY, paint)
    }

    private fun drawPage2(canvas: Canvas, ba: BeritaAcaraItem) {
        val paint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        val boldPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
        }

        val regularPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 9f
        }

        // Judul Tanda Tangan
        boldPaint.textSize = 10f
        drawCenteredText(canvas, "PIHAK YANG MELAKSANAKAN / MENYAKSIKAN PEMUSNAHAN:", boldPaint, 297.5f, 60f)

        // Pisahkan saksi dan penanggung jawab
        val penanggungJawab = ba.penandatangan.filter { it.role == "PENANGGUNG_JAWAB" }
        val saksiSaksi = ba.penandatangan.filter { it.role != "PENANGGUNG_JAWAB" }

        // Kolom Kiri: Saksi-Saksi
        var leftY = 100f
        saksiSaksi.forEachIndexed { index, saksi ->
            val roleLabel = when (saksi.role) {
                "SAKSI_1" -> "Saksi I"
                "SAKSI_2" -> "Saksi II"
                else -> "Saksi Kearsipan"
            }
            drawCenteredText(canvas, roleLabel, boldPaint, 170f, leftY)
            leftY += 14f
            drawCenteredText(canvas, saksi.jabatan, regularPaint, 170f, leftY)
            leftY += 55f // Ruang tanda tangan
            drawCenteredText(canvas, saksi.nama, boldPaint, 170f, leftY)
            // Draw underline for name
            val nameWidth = boldPaint.measureText(saksi.nama)
            canvas.drawLine(170f - (nameWidth / 2f), leftY + 2f, 170f + (nameWidth / 2f), leftY + 2f, regularPaint)
            leftY += 40f
        }

        // Kolom Kanan: Penanggung Jawab
        var rightY = 100f
        penanggungJawab.forEach { pj ->
            drawCenteredText(canvas, "Pihak Penanggung Jawab,", boldPaint, 425f, rightY)
            rightY += 14f
            drawCenteredText(canvas, pj.jabatan, regularPaint, 425f, rightY)
            rightY += 55f // Ruang tanda tangan
            drawCenteredText(canvas, pj.nama, boldPaint, 425f, rightY)
            // Draw underline for name
            val nameWidth = boldPaint.measureText(pj.nama)
            canvas.drawLine(425f - (nameWidth / 2f), rightY + 2f, 425f + (nameWidth / 2f), rightY + 2f, regularPaint)
            rightY += 40f
        }
    }

    private fun drawPage3(context: Context, canvas: Canvas, ba: BeritaAcaraItem) {
        val paint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
        }

        // Header Judul Lampiran
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        drawCenteredText(canvas, "LAMPIRAN DOKUMENTASI PEMUSNAHAN ARSIP", paint, 297.5f, 60f)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        drawCenteredText(canvas, "Nomor B.A.: ${ba.nomor}  |  Tanggal Eksekusi: ${ba.tanggal}", paint, 297.5f, 76f)

        // Garis Pembatas Header
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawLine(54f, 90f, 541f, 90f, paint)

        // Decode & Gambar Bitmap Foto-Foto Dokumentasi (Maks 3 Foto)
        val fotoList = ba.fotoDokumentasiList
        if (fotoList.isNotEmpty()) {
            val count = fotoList.size
            var currentY = 110f

            val slotHeight = when (count) {
                1 -> 450f
                2 -> 240f
                else -> 170f
            }

            fotoList.take(3).forEachIndexed { index, photoUriStr ->
                try {
                    val uri = android.net.Uri.parse(photoUriStr)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    if (originalBitmap != null) {
                        val availableWidth = 487f // 595 - 54 - 54
                        val maxPhotoHeight = slotHeight - 20f
                        val aspect = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()

                        var targetW = availableWidth
                        var targetH = targetW / aspect
                        if (targetH > maxPhotoHeight) {
                            targetH = maxPhotoHeight
                            targetW = targetH * aspect
                        }

                        val startX = 54f + (availableWidth - targetW) / 2f
                        val startY = currentY + (maxPhotoHeight - targetH) / 2f

                        val destRect = RectF(startX, startY, startX + targetW, startY + targetH)
                        val srcRect = Rect(0, 0, originalBitmap.width, originalBitmap.height)

                        paint.style = Paint.Style.FILL
                        canvas.drawBitmap(originalBitmap, srcRect, destRect, paint)

                        // Bingkai foto
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 1f
                        paint.color = Color.GRAY
                        canvas.drawRect(destRect, paint)

                        // Keterangan di bawah foto
                        paint.style = Paint.Style.FILL
                        paint.color = Color.DKGRAY
                        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                        paint.textSize = 8.5f
                        val caption = if (count == 1) {
                            "Dokumentasi Fisik Pelaksanaan Pemusnahan Arsip di ${ba.lokasi}"
                        } else {
                            "Foto Dokumentasi ${index + 1}: Pelaksanaan Pemusnahan Arsip di ${ba.lokasi}"
                        }
                        drawCenteredText(canvas, caption, paint, 297.5f, startY + targetH + 12f)
                    }
                } catch (e: Exception) {
                    paint.style = Paint.Style.FILL
                    paint.color = Color.RED
                    paint.textSize = 9f
                    drawCenteredText(canvas, "Foto ${index + 1} gagal dimuat", paint, 297.5f, currentY + (slotHeight / 2f))
                }
                currentY += slotHeight + 20f
            }
        }
    }

    private fun drawCenteredText(canvas: Canvas, text: String, paint: Paint, centerX: Float, y: Float) {
        val width = paint.measureText(text)
        canvas.drawText(text, centerX - (width / 2f), y, paint)
    }

    private fun drawWrappedText(canvas: Canvas, text: String, textPaint: TextPaint, width: Int, x: Float, y: Float): Float {
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.15f)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(x, y)
        staticLayout.draw(canvas)
        canvas.restore()
        return staticLayout.height.toFloat()
    }
}
