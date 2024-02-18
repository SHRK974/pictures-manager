package com.example.pictsmanager.presentation.album

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.AlbumRequestDto
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.domain.repository.AlbumRepository
import com.example.pictsmanager.domain.repository.ImageRepository
import com.example.pictsmanager.domain.repository.UserRepository
import com.example.pictsmanager.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
): ViewModel() {
    var state by mutableStateOf(AlbumState())
        private set
    var imagesLoaded = mutableStateMapOf<String, ImageDto>()
        private set
    var userQueried = mutableStateListOf<UserDto>()
        private set
    fun createAlbum(name: String) {
        viewModelScope.launch {
            when (val result = albumRepository.createAlbum(
                AlbumRequestDto(label = name)
            )) {
                is Resource.Success -> {
                    loadUserAlbums()
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    fun loadUserAlbums() {
        viewModelScope.launch {
            state = state.copy(isLoading = false, error = null)

            when (val result = albumRepository.getUserAlbums()) {
                is Resource.Success -> {
                    result.data?.let {
                        state = state.copy(
                            albums = it,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    state = state.copy(
                        albums = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    fun loadAlbumsSharedWithUser() {
        viewModelScope.launch {
            state = state.copy(isLoading = false, error = null)

            when (val result = albumRepository.getAlbumsSharedWithUser()) {
                is Resource.Success -> {
                    result.data?.let {
                        state = state.copy(
                            albumsSharedWithMe = it,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    state = state.copy(
                        albums = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    fun loadAlbumsSharedWithOtherUsers() {
        viewModelScope.launch {
            state = state.copy(isLoading = false, error = null)

            when (val result = albumRepository.getAlbumsSharedWithOtherUsers()) {
                is Resource.Success -> {
                    result.data?.let {
                        state = state.copy(
                            albumSharedWithOther = it,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    state = state.copy(
                        albums = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    fun searchUsersByEmail(query: String) {
        viewModelScope.launch {
            when (val result = userRepository.searchUsersByEmail(
                query
            )) {
                is Resource.Success -> {
                    userQueried.clear()
                    result.data?.forEach {
                        userQueried.add(it)
                    }
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    fun addImageToAlbum(
        albumId: String,
        imageId: String
    ) {
        viewModelScope.launch {
            when (val result = albumRepository.addImageToAlbum(
                albumId = albumId,
                imageId = imageId
            )) {
                is Resource.Success -> {
                    state = state.copy(
                        error = null,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    fun shareAlbumWith(
        albumId: String,
        toUserId: String
    ) {
        viewModelScope.launch {
            when (val result = albumRepository.shareAlbumWIth(
                albumId = albumId,
                toUserId = toUserId
            )) {
                is Resource.Success -> {
                    state = state.copy(
                        error = null,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    fun loadImage(id: String) {
        viewModelScope.launch {
            if (imagesLoaded.contains(id)) {
                return@launch
            }
            when (val result = imageRepository.getImageById(id)) {
                is Resource.Success -> {
                    val imageDto = result.data ?: return@launch
                    imageDto.compressedBase64?.let { base64 ->
                        val decodedBitmap = Base64.decode(base64, Base64.DEFAULT)
                            .let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                        imageDto.decodedBitmap = decodedBitmap
                    } ?: run {
                        imageDto.base64?.let { base64 ->
                            val decodedBitmap = Base64.decode(base64, Base64.DEFAULT)
                                .let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                            imageDto.decodedBitmap = decodedBitmap
                        }
                    }
                    state = state.copy(
                        error = null,
                    )
                    imagesLoaded[id] = imageDto
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message
                    )
                }
            }
        }
    }
    fun deleteImage(id: String)  {
        viewModelScope.launch {
            getAllImagesAlbumFromState()?.let {
                when (val result = albumRepository.deleteImageById(
                    albumId = it.id,
                    imageId = id
                )) {
                    is Resource.Success -> {
                        reloadWhenNewUpload(true)
                        state = state.copy(
                            error = null,
                        )
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            error = result.message
                        )
                    }
                }
            }

        }
    }
    fun getAllImagesAlbumFromState(): AlbumDto? {
        return state.albums?.find { it.label == "All Images" }
    }
    fun reloadWhenNewUpload(action: Boolean) {
        if (action) {
            loadUserAlbums()
        }
    }
}