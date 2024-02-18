package com.example.pictsmanager.presentation.user

import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.data.remote.authentication.AuthResponseDto


data class UserState(
    val user: UserDto? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
