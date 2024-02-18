package com.example.pictsmanager.data.remote

data class AlbumDto(
    val id: String,
    val label: String,
    val canDelete: Boolean,
    var images: List<ImageDto>,
)
