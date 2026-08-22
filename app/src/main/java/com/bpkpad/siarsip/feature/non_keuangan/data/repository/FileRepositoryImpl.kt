package com.bpkpad.arsip.data.repository

import com.bpkpad.arsip.domain.repository.FileRepository
import com.bpkpad.arsip.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor() : FileRepository {

    override fun uploadImage(imageBytes: ByteArray): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val fileName = "file:///local_storage/${UUID.randomUUID()}.jpg"
            emit(ResultState.Success(fileName))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Upload failed", e))
        }
    }

    override fun extractTextFromImage(imageBytes: ByteArray): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (bitmap == null) {
                trySend(ResultState.Error("Gagal membaca gambar dokumen"))
                close()
                return@callbackFlow
            }
            
            trySend(ResultState.Success("Extracted text from document"))
            close()
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Error OCR", e))
            close()
        }
        awaitClose { }
    }
}
