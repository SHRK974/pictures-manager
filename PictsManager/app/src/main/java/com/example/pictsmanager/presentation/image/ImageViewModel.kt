package com.example.pictsmanager.presentation.image

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.domain.repository.AlbumRepository
import com.example.pictsmanager.domain.repository.ImageRepository
import com.example.pictsmanager.domain.util.Resource
import com.example.pictsmanager.presentation.album.AlbumViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    var state by mutableStateOf(ImagePickerManagerState())
        private set

    fun upload(
        originalImageFile: File,
        compressedImageFile: File
    ) {
        viewModelScope.launch {
            state = state.copy(
                wasUploadSuccessful = false,
                hasImage = false,
                imageUri = null
            )

            val requestBodyOriginal = originalImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val partOriginal = MultipartBody.Part.createFormData("originalImageFile", originalImageFile.name, requestBodyOriginal)

            val requestBodyCompressed = compressedImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val partCompressed = MultipartBody.Part.createFormData("compressedImageFile", compressedImageFile.name, requestBodyCompressed)

            when (val result = albumRepository.uploadImage(partOriginal, partCompressed)) {
                is Resource.Success -> {
                    Log.d("Image Upload", "Success")
                    state = state.copy(
                        wasUploadSuccessful = true,
                    )
                }
                is Resource.Error -> {
                    Log.d("Image Upload", "Failed")
                    state = state.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    fun setUri(fileUri: Uri?) {
        state = state.copy(
            imageUri = fileUri
        )
    }
}