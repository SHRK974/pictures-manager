package com.example.pictsmanager.domain.repository

import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.domain.util.Resource

interface ImageRepository {
    suspend fun getImageById(id: String): Resource<ImageDto>
}