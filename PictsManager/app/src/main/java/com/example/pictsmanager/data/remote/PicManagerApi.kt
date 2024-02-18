package com.example.pictsmanager.data.remote

import com.example.pictsmanager.data.remote.authentication.AuthRequestDto
import com.example.pictsmanager.data.remote.authentication.AuthResponseDto
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PicManagerApi {
    @POST("user/login")
    suspend fun login(@Body request: AuthRequestDto): AuthResponseDto
    @POST("album")
    suspend fun createAlbum(@Body request: AlbumRequestDto): ResponseBody
    @POST("album/{albumId}/{imageId}")
    suspend fun addImageToAlbum(
        @Path("albumId") albumId: String,
        @Path("imageId") imageId: String
    ): ResponseBody
    @GET("user/search/{query}")
    suspend fun searchUsersByEmail(@Path("query") query: String): List<UserDto>
    @GET("user/albums")
    suspend fun getUserAlbums(): List<AlbumDto>
    @POST("user/albums/share/{albumId}/{toUserId}")
    suspend fun shareAlbumWith(
        @Path("albumId") albumId: String,
        @Path("toUserId") toUserId: String
    ): ResponseBody
    @GET("user/albums/sharedWithMe")
    suspend fun getAlbumsSharedWithUser(): List<AlbumDto>
    @GET("user/albums/sharedWithOthers")
    suspend fun getAlbumsSharedWithOtherUsers(): List<AlbumDto>
    @Multipart
    @POST("album/upload")
    suspend fun uploadImage(
        @Part originalImageFile: MultipartBody.Part,
        @Part compressedImageFile: MultipartBody.Part
    ): ResponseBody
    @GET("image/{id}")
    suspend fun getImageById(@Path("id") id: String): ImageDto
    @DELETE("album/{albumId}/{imageId}")
    suspend fun deleteImageById(
        @Path("albumId") albumId: String,
        @Path("imageId") imageId: String
    ): ResponseBody
}