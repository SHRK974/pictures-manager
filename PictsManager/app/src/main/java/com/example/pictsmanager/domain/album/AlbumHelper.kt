package com.example.pictsmanager.domain.album

import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.ImageDto
import java.time.LocalDateTime

data class AlbumHelper (
    val albumPerEditStatus: Map<Boolean, List<AlbumDto>>,
    val albumImagesPerDay: Map<LocalDateTime, List<ImageDto>>
)