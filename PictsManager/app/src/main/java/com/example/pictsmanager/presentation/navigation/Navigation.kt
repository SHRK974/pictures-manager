package com.example.pictsmanager.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pictsmanager.presentation.album.*
import com.example.pictsmanager.presentation.image.ImageViewModel
import com.example.pictsmanager.presentation.user.UserViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    albumViewModel: AlbumViewModel,
    imageViewModel: ImageViewModel,
) {
    NavHost(navController = navController, startDestination = Screen.PhotosScreen.route) {
        composable(Screen.PhotosScreen.route) {
            albumViewModel.reloadWhenNewUpload(imageViewModel.state.wasUploadSuccessful)
            AlbumDetailGrid(albumViewModel = albumViewModel)
        }
        composable(Screen.AlbumsScreen.route) {
            AlbumsGrid(albumViewModel = albumViewModel)
        }
        composable(Screen.SharedScreen.route) {
            albumViewModel.loadAlbumsSharedWithUser()
            albumViewModel.loadAlbumsSharedWithOtherUsers()
            TwoSectionGrid(albumViewModel = albumViewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<Screen>,
    navController: NavController,
    modifier: Modifier,
    onItemClick: (Screen) -> Unit
) {
    val backStateEntry = navController.currentBackStackEntryAsState();
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.background,
        elevation = 50.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStateEntry.value?.destination?.route;
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = Color.Blue,
                unselectedContentColor = Color.LightGray,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        item.iconId?.let { painterResource(id = it) }
                            ?.let { Icon(
                                painter = it,
                                contentDescription = item.name,
                                tint = Color.White
                            ) }
                        if (selected) {
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            )

        }
    }
}