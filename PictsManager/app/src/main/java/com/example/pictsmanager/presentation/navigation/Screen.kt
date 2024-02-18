package com.example.pictsmanager.presentation.navigation

import com.example.pictsmanager.R

sealed class Screen(
    val name: String,
    val route: String,
    val iconId: Int?
    ) {
    object LoginScreen : Screen(
        "Login",
        "login",
        null
    )
    object PhotosScreen : Screen(
        "Photos",
        "photos",
        R.drawable.outline_insert_photo_24
    )
    object AlbumsScreen : Screen(
        "Albums",
        "albums",
        R.drawable.outline_folder_24
    )
    object SharedScreen : Screen(
        "Shared",
        "shared",
        R.drawable.outline_folder_shared_24
    )
}
