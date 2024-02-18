package com.example.pictsmanager.domain.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import com.example.pictsmanager.MainActivity
import com.example.pictsmanager.presentation.image.ImageViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class ImagePickerManager(
    private val activity: MainActivity,
    private var imageViewModel: ImageViewModel
) {
    fun setGalleryActivityResult() : ImagePickerManager {
        activity.filePickerLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == ComponentActivity.RESULT_OK) {
                it.data?.data?.let { uri -> getFileFromUri(uri)?.let { file ->
                    compressImage(file)?.let { compressedFile ->
                        imageViewModel.upload(
                            file,
                            compressedFile
                        )
                    }
                } }
            } else if (it.resultCode == ComponentActivity.RESULT_CANCELED) {
                Toast.makeText(activity.applicationContext, "File picker canceled", Toast.LENGTH_SHORT).show()
            }
        }
        return this
    }
    fun setCameraActivityResult() : ImagePickerManager{
        activity.cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                imageViewModel.state.imageUri?.let { getFileFromUri(it)?.let { file ->
                    compressImage(file)?.let { compressedFile ->
                        imageViewModel.upload(
                            file,
                            compressedFile
                        )
                    }
                } }
                Log.w("Camera api", "OK")
            } else {
                Log.w("Camera api", "Not OK")
            }
        }
        return this
    }
    private fun compressImage(inputFile: File): File? {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(inputFile.path, options)

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            val scaleFactor = calculateScaleFactor(srcWidth, srcHeight)

            options.inJustDecodeBounds = false
            options.inSampleSize = scaleFactor
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val bitmap = BitmapFactory.decodeFile(inputFile.path, options)

            val outputFile = File.createTempFile("compressed", ".jpg")
            val outputStream = FileOutputStream(outputFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, outputStream)

            outputStream.flush()
            outputStream.close()
            println("file size ${outputFile.length()}")
            return outputFile

        } catch (ex: Exception) {
            Log.e("ImageCompression", "Failed to compress image", ex)
            return null
        }
    }
    private fun calculateScaleFactor(srcWidth: Int, srcHeight: Int): Int {
        val reqWidth = 1080
        val reqHeight = 1920
        var inSampleSize = 1

        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            val heightRatio = (srcHeight.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (srcWidth.toFloat() / reqWidth.toFloat()).roundToInt()

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }
    private fun getFileFromUri(uri: Uri): File? {
        val contentResolver = activity.applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
        val file = File.createTempFile("original", ".$fileExtension")
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        return file
    }
}