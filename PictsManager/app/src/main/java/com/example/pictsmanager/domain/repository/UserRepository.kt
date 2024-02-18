package com.example.pictsmanager.domain.repository

import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.data.remote.authentication.AuthRequestDto
import com.example.pictsmanager.data.remote.authentication.AuthResponseDto
import com.example.pictsmanager.domain.util.Resource

interface UserRepository {
    suspend fun login(request: AuthRequestDto): Resource<AuthResponseDto>
    suspend fun searchUsersByEmail(query: String): Resource<List<UserDto>>
}