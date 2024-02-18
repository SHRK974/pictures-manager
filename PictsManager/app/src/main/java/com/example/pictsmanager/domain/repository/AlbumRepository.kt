package com.example.pictsmanager.domain.repository

import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.AlbumRequestDto
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.domain.util.Resource
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody

interface AlbumRepository {
    suspend fun createAlbum(request: AlbumRequestDto): Resource<ResponseBody>
    suspend fun getUserAlbums(): Resource<List<AlbumDto>>
    suspend fun addImageToAlbum(
        albumId: String,
        imageId: String
    ): Resource<ResponseBody>
    suspend fun shareAlbumWIth(
        albumId: String,
        toUserId: String
    ): Resource<ResponseBody>
    suspend fun getAlbumsSharedWithUser(): Resource<List<AlbumDto>>
    suspend fun getAlbumsSharedWithOtherUsers(): Resource<List<AlbumDto>>
    suspend fun uploadImage(
        originalImageFile: MultipartBody.Part,
        compressedImageFile: MultipartBody.Part
    ): Resource<ResponseBody>
    suspend fun deleteImageById(
        albumId: String,
        imageId: String
    ): Resource<ResponseBody>
}