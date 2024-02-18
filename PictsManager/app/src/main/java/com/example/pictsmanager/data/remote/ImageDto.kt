package com.example.pictsmanager.data.remote

import android.graphics.Bitmap
import java.util.Base64
import java.util.Date

data class ImageDto(
    val id: String,
    val label: String,
    val createdAt: Date,
    val extension: String?,
    var base64: String?,
    var compressedBase64: String?,
    @Transient var decodedBitmap: Bitmap?
)
