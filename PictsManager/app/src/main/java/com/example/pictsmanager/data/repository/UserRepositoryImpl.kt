package com.example.pictsmanager.data.repository

import com.example.pictsmanager.data.remote.PicManagerApi
import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.data.remote.authentication.AuthRequestDto
import com.example.pictsmanager.data.remote.authentication.AuthResponseDto
import com.example.pictsmanager.domain.repository.UserRepository
import com.example.pictsmanager.domain.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: PicManagerApi
): UserRepository {
    override suspend fun login(request: AuthRequestDto): Resource<AuthResponseDto> {
        return try {
            Resource.Success(
                data = api.login(request = request)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred during login.")
        }
    }
    override suspend fun searchUsersByEmail(query: String): Resource<List<UserDto>> {
        return try {
            Resource.Success(
                data = api.searchUsersByEmail(query = query)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}