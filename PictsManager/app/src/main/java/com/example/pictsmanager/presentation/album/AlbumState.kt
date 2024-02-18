package com.example.pictsmanager.presentation.album

import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.ImageDto

data class AlbumState(
    val albums: List<AlbumDto>? = null,
    val albumsSharedWithMe: List<AlbumDto>? = null,
    val albumSharedWithOther: List<AlbumDto>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
