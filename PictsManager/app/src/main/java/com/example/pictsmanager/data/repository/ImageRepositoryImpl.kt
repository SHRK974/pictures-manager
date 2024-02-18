package com.example.pictsmanager.data.repository

import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.data.remote.PicManagerApi
import com.example.pictsmanager.domain.repository.ImageRepository
import com.example.pictsmanager.domain.util.Resource
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val api: PicManagerApi
): ImageRepository {
    override suspend fun getImageById(id: String): Resource<ImageDto> {
        return try {
            Resource.Success(
                data = api.getImageById(id)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occued.")
        }
    }
}