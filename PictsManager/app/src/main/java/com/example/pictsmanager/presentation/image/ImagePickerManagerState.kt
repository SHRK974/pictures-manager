package com.example.pictsmanager.presentation.image

import android.net.Uri
import com.example.pictsmanager.data.remote.ImageDto

data class ImagePickerManagerState(
    var hasImage: Boolean = false,
    var wasUploadSuccessful: Boolean = false,
    var imageUri: Uri? = null,
    var error: String? = null
)
