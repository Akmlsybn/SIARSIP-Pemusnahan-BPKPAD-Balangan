package com.bpkpad.peminjaman.core.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.bpkpad.peminjaman.core.common.Constants
import com.bpkpad.peminjaman.core.common.ResultState
import java.io.ByteArrayOutputStream

class SupabaseFileRepository(
    private val context: Context
) : FileRepository {

    override suspend fun uploadImage(localUri: Uri, remotePath: String): ResultState<String> {
        return ResultState.Success(localUri.toString())
    }

    override suspend fun deleteFile(remotePath: String): ResultState<Unit> {
        return ResultState.Success(Unit)
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        return if (bitmap.width > Constants.MAX_IMAGE_WIDTH || bitmap.height > Constants.MAX_IMAGE_HEIGHT) {
            val ratio = minOf(
                Constants.MAX_IMAGE_WIDTH.toFloat() / bitmap.width,
                Constants.MAX_IMAGE_HEIGHT.toFloat() / bitmap.height
            )
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap
    }
}
