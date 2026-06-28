package com.bpkpad.siarsip.core.utils

import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

object ExcelExportManager {
    fun exportToExcel(berkas: BerkasUsulMusnah, outputStream: OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Arsip Usul Musnah")

        // Aktifkan tampilan garis grid
        sheet.isDisplayGridlines = true

        // ── Font setup ──────────────────────────────────────────────
        val titleFont = workbook.createFont().apply {
            fontName = "Arial"
            fontHeightInPoints = 14
            bold = true
        }

        val headerFont = workbook.createFont().apply {
            fontName = "Arial"
            fontHeightInPoints = 11
            bold = true
            color = IndexedColors.WHITE.index
        }

        val dataFont = workbook.createFont().apply {
            fontName = "Arial"
            fontHeightInPoints = 10
        }

        // ── Cell styles setup ───────────────────────────────────────
        val titleStyle = workbook.createCellStyle().apply {
            setFont(titleFont)
            alignment = HorizontalAlignment.LEFT
        }

        val headerStyle = workbook.createCellStyle().apply {
            setFont(headerFont)
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillForegroundColor = IndexedColors.GREEN.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        val dataStyleCentered = workbook.createCellStyle().apply {
            setFont(dataFont)
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        val dataStyleLeft = workbook.createCellStyle().apply {
            setFont(dataFont)
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        // ── Metadata Header ─────────────────────────────────────────
        var rowIdx = 0
        
        val titleRow = sheet.createRow(rowIdx++)
        val titleCell = titleRow.createCell(0)
        titleCell.setCellValue("DAFTAR ARSIP USUL MUSNAH")
        titleCell.setCellStyle(titleStyle)
        
        rowIdx++ // spacer

        val metaRow1 = sheet.createRow(rowIdx++)
        metaRow1.createCell(0).apply { setCellValue("Nomor Berkas:"); setCellStyle(dataStyleLeft) }
        metaRow1.createCell(1).apply { setCellValue(berkas.nomorBerkas); setCellStyle(dataStyleLeft) }

        val metaRow2 = sheet.createRow(rowIdx++)
        metaRow2.createCell(0).apply { setCellValue("Tanggal Berkas:"); setCellStyle(dataStyleLeft) }
        metaRow2.createCell(1).apply { setCellValue(berkas.tanggal); setCellStyle(dataStyleLeft) }

        val metaRow3 = sheet.createRow(rowIdx++)
        metaRow3.createCell(0).apply { setCellValue("Perihal:"); setCellStyle(dataStyleLeft) }
        metaRow3.createCell(1).apply { setCellValue(berkas.perihal); setCellStyle(dataStyleLeft) }

        val metaRow4 = sheet.createRow(rowIdx++)
        metaRow4.createCell(0).apply { setCellValue("Unit Pengolah:"); setCellStyle(dataStyleLeft) }
        metaRow4.createCell(1).apply { setCellValue(berkas.unitPengolah); setCellStyle(dataStyleLeft) }

        rowIdx++ // spacer

        // ── Tabel Header (2 Baris Tergabung) ────────────────────────
        val tableHeaderStartRow = rowIdx

        val headerRow1 = sheet.createRow(rowIdx++)
        val headerRow2 = sheet.createRow(rowIdx++)

        val headers = listOf(
            "No.", "Kode Klasifikasi", "Isi Informasi", "Kurun Waktu", 
            "Tingkat Perkembangan", "Volume", "Retensi", "", "Keterangan"
        )

        for (col in 0..8) {
            val cell1 = headerRow1.createCell(col)
            cell1.setCellStyle(headerStyle)
            if (col != 6 && col != 7) {
                cell1.setCellValue(headers[col])
            }
            val cell2 = headerRow2.createCell(col)
            cell2.setCellStyle(headerStyle)
        }

        headerRow1.getCell(6).setCellValue("Retensi")
        headerRow2.getCell(6).setCellValue("Aktif")
        headerRow2.getCell(7).setCellValue("Inaktif")

        // Melakukan penggabungan (merge) sel
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 0, 0)) // No.
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 1, 1)) // Kode Klasifikasi
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 2, 2)) // Isi Informasi
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 3, 3)) // Kurun Waktu
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 4, 4)) // Tingkat Perkembangan
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 5, 5)) // Volume
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow, 6, 7))     // Retensi (Aktif & Inaktif)
        sheet.addMergedRegion(CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 8, 8)) // Keterangan

        // ── Menulis Data Baris ──────────────────────────────────────
        var dataNo = 1
        for (archive in berkas.archives) {
            val row = sheet.createRow(rowIdx++)
            
            row.createCell(0).apply { setCellValue(dataNo++.toString()); setCellStyle(dataStyleCentered) }
            row.createCell(1).apply { setCellValue(archive.fullKode); setCellStyle(dataStyleCentered) }
            row.createCell(2).apply { setCellValue(archive.deskripsi); setCellStyle(dataStyleLeft) }
            row.createCell(3).apply { setCellValue(archive.tahun); setCellStyle(dataStyleCentered) }
            row.createCell(4).apply { setCellValue(archive.tingkat); setCellStyle(dataStyleCentered) }
            row.createCell(5).apply { setCellValue(archive.volume); setCellStyle(dataStyleCentered) }
            row.createCell(6).apply { setCellValue(archive.retensiAktif); setCellStyle(dataStyleCentered) }
            row.createCell(7).apply { setCellValue(archive.retensiInaktif); setCellStyle(dataStyleCentered) }
            row.createCell(8).apply { setCellValue(archive.keterangan); setCellStyle(dataStyleLeft) }
        }

        // Set lebar kolom secara manual untuk menghindari ketergantungan java.awt (crash di Android)
        sheet.setColumnWidth(0, 6 * 256)   // No.
        sheet.setColumnWidth(1, 35 * 256)  // Kode Klasifikasi
        sheet.setColumnWidth(2, 50 * 256)  // Isi Informasi
        sheet.setColumnWidth(3, 15 * 256)  // Kurun Waktu
        sheet.setColumnWidth(4, 22 * 256)  // Tingkat Perkembangan
        sheet.setColumnWidth(5, 15 * 256)  // Volume
        sheet.setColumnWidth(6, 12 * 256)  // Retensi Aktif
        sheet.setColumnWidth(7, 12 * 256)  // Retensi Inaktif
        sheet.setColumnWidth(8, 18 * 256)  // Keterangan

        // Tulis workbook ke stream tujuan
        workbook.write(outputStream)
        workbook.close()
    }
}
