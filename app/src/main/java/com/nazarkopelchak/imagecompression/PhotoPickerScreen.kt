package com.nazarkopelchak.imagecompression

import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun PhotoPickerScreen(
    imageCompressor: ImageCompressor,
    fileManager: FileManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { contentUri ->
        if (contentUri == null) {
            return@rememberLauncherForActivityResult
        }

        val mimeType = context.contentResolver.getType(contentUri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        scope.launch {
            fileManager.saveImage(
                contentUri,
                "uncompressed_image.$extension"
            )
        }

        scope.launch {
            val compressedImage = imageCompressor.compressImage(
                uri = contentUri,
                compressionThreshold = 200 * 1024L  // 200kb
            )
            fileManager.saveImage(
                bytes = compressedImage ?: return@launch,
                filename = "compressed_image.$extension"
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                photoPicker.launch(
                    input = PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        ) {
            Text(
                text = "Pick an image"
            )
        }
    }
}