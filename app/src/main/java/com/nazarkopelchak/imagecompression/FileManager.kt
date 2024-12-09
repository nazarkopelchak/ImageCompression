package com.nazarkopelchak.imagecompression

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManager(
    private val context: Context
) {

    suspend fun saveImage(
        uri: Uri,
        filename: String
    ) {
        withContext(Dispatchers.IO) {
            context.contentResolver
                .openInputStream(uri)?.use { inputStream ->
                    context.openFileOutput(filename, Context.MODE_PRIVATE)
                        .use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                }
        }
    }

    suspend fun saveImage(
        bytes: ByteArray,
        filename: String
    ) {
        withContext(Dispatchers.IO) {
            context
                .openFileOutput(filename, Context.MODE_PRIVATE)
                    .use { outputStream ->
                        outputStream.write(bytes)
                }
        }
    }
}