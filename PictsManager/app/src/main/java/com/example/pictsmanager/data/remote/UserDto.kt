package com.example.pictsmanager.data.remote

data class UserDto(
    val id: String,
    val email: String,
    val password: String,
    val albums: List<AlbumDto>,
    val sharedWithOther: List<AlbumDto>,
    val sharedWithMe: List<AlbumDto>
)
