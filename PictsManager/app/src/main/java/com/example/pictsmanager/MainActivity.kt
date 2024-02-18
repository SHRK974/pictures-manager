package com.example.pictsmanager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.pictsmanager.domain.image.ImagePickerManager
import com.example.pictsmanager.domain.util.AppPreferences
import com.example.pictsmanager.domain.util.ComposeFileProvider
import com.example.pictsmanager.presentation.album.AlbumViewModel
import com.example.pictsmanager.presentation.image.ImageViewModel
import com.example.pictsmanager.presentation.image.UploadImageButton
import com.example.pictsmanager.presentation.navigation.BottomNavigationBar
import com.example.pictsmanager.presentation.navigation.Navigation
import com.example.pictsmanager.presentation.navigation.Screen
import com.example.pictsmanager.presentation.ui.theme.PictsManagerTheme
import com.example.pictsmanager.presentation.user.LoginView
import com.example.pictsmanager.presentation.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val albumViewModel: AlbumViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPreferences.setup(applicationContext)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (AppPreferences.accessTokenAsString !== null) {
                albumViewModel.loadUserAlbums()
            }
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ))

        ImagePickerManager(
            activity = this,
            imageViewModel = imageViewModel,
        )
            .setGalleryActivityResult()
            .setCameraActivityResult()

        setContent {
            PictsManagerTheme {
                val rememberAccessToken by remember { mutableStateOf(AppPreferences.accessTokenAsString) }
                if (!userViewModel.state.isLoggedIn || rememberAccessToken.isNullOrEmpty()) {
                    Scaffold {
                       LoginView(
                           modifier = Modifier,
                           userViewModel = userViewModel,
                           context = applicationContext
                       )
                    }
                } else {
                    albumViewModel.loadUserAlbums()
                    val navController = rememberNavController();

                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(
                                items = listOf(
                                    Screen.PhotosScreen,
                                    Screen.AlbumsScreen,
                                    Screen.SharedScreen
                                ),
                                navController = navController,
                                modifier = Modifier,
                                onItemClick = {
                                    navController.navigate(it.route)
                                }
                            )
                        },
                        floatingActionButton = {
                            UploadImageButton(
                                onCameraClick = {
                                    val uri = ComposeFileProvider.getImageUri(context = applicationContext)
                                    imageViewModel.setUri(uri)
                                    cameraLauncher.launch(uri)
                                },
                                onGalleryClick = {

                                    val intent = Intent(Intent.ACTION_PICK).apply {
                                        type = "image/*"
                                    }
                                    filePickerLauncher.launch(intent)
                                },
                                onFolderClick = {
                                    albumViewModel.createAlbum("Album${UUID.randomUUID().toString().substring(0, 5)}")
                                }
                            )
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        isFloatingActionButtonDocked = false
                    ) {
                        Navigation(
                            navController = navController,
                            albumViewModel = albumViewModel,
                            imageViewModel = imageViewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!", color = Color.Black)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PictsManagerTheme {
        Greeting("Android")
    }
}