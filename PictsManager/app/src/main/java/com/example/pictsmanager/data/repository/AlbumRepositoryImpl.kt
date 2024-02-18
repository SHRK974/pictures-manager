package com.example.pictsmanager.data.repository

import com.example.pictsmanager.data.remote.*
import com.example.pictsmanager.domain.repository.AlbumRepository
import com.example.pictsmanager.domain.util.Resource
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val api: PicManagerApi
): AlbumRepository {
    override suspend fun createAlbum(request: AlbumRequestDto): Resource<ResponseBody> {
        return try {
            Resource.Success(
                data = api.createAlbum(request)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun getUserAlbums(): Resource<List<AlbumDto>> {
        return try {
            Resource.Success(
                data = api.getUserAlbums()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun addImageToAlbum(
        albumId: String,
        imageId: String
    ): Resource<ResponseBody> {
        return try {
            Resource.Success(
                data = api.addImageToAlbum(albumId = albumId, imageId = imageId)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun shareAlbumWIth(
        albumId: String,
        toUserId: String
    ): Resource<ResponseBody> {
        return try {
            Resource.Success(
                data = api.shareAlbumWith(albumId = albumId, toUserId = toUserId)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun getAlbumsSharedWithUser(): Resource<List<AlbumDto>> {
        return try {
            Resource.Success(
                data = api.getAlbumsSharedWithUser()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun getAlbumsSharedWithOtherUsers(): Resource<List<AlbumDto>> {
        return try {
            Resource.Success(
                data = api.getAlbumsSharedWithOtherUsers()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun uploadImage(
        originalImageFile: MultipartBody.Part,
        compressedImageFile: MultipartBody.Part
    ): Resource<ResponseBody> {
        return try {
            Resource.Success(
                data = api.uploadImage(
                    originalImageFile = originalImageFile,
                    compressedImageFile = compressedImageFile
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
    override suspend fun deleteImageById(
        albumId: String,
        imageId: String
    ): Resource<ResponseBody> {
        return try {
            Resource.Success(
                data = api.deleteImageById(
                    albumId = albumId,
                    imageId = imageId
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}