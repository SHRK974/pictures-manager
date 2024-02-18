package com.example.pictsmanager.presentation.user

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.data.remote.authentication.AuthRequestDto
import com.example.pictsmanager.domain.repository.UserRepository
import com.example.pictsmanager.domain.util.AppPreferences
import com.example.pictsmanager.domain.util.Resource
import com.example.pictsmanager.presentation.album.AlbumViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    var state by mutableStateOf(UserState())
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = false, error = null)

            when (val result = userRepository.login(
                AuthRequestDto(email, password)
            )) {
                is Resource.Success -> {
                    AppPreferences.accessTokenAsString = result.data?.token.toString()
                    state = state.copy(
                        isLoggedIn = true,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun logout() {
        AppPreferences.accessTokenAsString = null
        state = state.copy(
            isLoggedIn = false
        )
    }

    init {
        val accessToken = AppPreferences.accessTokenAsString
        if (accessToken !== null) {
            state = state.copy(
                isLoggedIn = true
            )
        }
    }
}