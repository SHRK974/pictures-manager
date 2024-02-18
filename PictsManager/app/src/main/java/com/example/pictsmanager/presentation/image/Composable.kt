package com.example.pictsmanager.presentation.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.*
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.pictsmanager.R
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.presentation.album.AlbumViewModel
import com.example.pictsmanager.presentation.ui.theme.Shapes

@Composable
fun ImageCard(
    imageDto: ImageDto,
    albumViewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    size: Dp,
    padding: Dp
) {
    LaunchedEffect(key1 = imageDto.id) {
        albumViewModel.loadImage(imageDto.id)
    }
    val imageDtoLoaded by rememberUpdatedState(albumViewModel.imagesLoaded[imageDto.id])
    var showDialog by remember { mutableStateOf(false) }
    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogAddToAlbum by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf("") }

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = imageDtoLoaded?.decodedBitmap ?: imageDtoLoaded?.compressedBase64 ?: imageDtoLoaded?.base64)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .apply(block = fun ImageRequest.Builder.() {
                crossfade(false)
            }).build()
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Action") },
            text = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            showDialogDelete = true
                        }
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = {
                            showDialogAddToAlbum = true
                        }) {
                        Text(text = "Add to an album")
                    }
                }

            },
            confirmButton = {

            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialogDelete = false
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
        if (showDialogDelete) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete image") },
                text = { Text("Are you sure you want to delete this image?") },
                confirmButton = {
                    Button(
                        onClick = {
                            albumViewModel.deleteImage(imageDto.id)
                            showDialogDelete = false
                            showDialog = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialogDelete = false
                            showDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (showDialogAddToAlbum) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Add Image to Album") },
                text = {
                    Column {
                        LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                            val albums = albumViewModel.state.albums
                            if (albums != null) {
                                items(albums.size) { index ->
                                    val album = albums[index]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = { selectedAlbum = album.id })
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = album.label,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Checkbox(
                                            checked = selectedAlbum == album.id,
                                            onCheckedChange = {
                                                selectedAlbum = if (it) {
                                                    album.id
                                                } else {
                                                    ""
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedAlbum.let {
                                albumViewModel.addImageToAlbum(
                                    albumId = selectedAlbum,
                                    imageId = imageDto.id
                                )
                                showDialogAddToAlbum = false
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Add Image")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialogAddToAlbum = false
                            showDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    Card(
        modifier = modifier
            .pointerInput(Unit) {
                if (isInteractive) {
                    detectTapGestures(
                        onLongPress = {
                            showDialog = true
                        }
                    )
                }
            }
            .size(size)
            .padding(all = padding)
            .aspectRatio(1f),
        shape = Shapes.large,
        elevation = 5.dp,
    ) {
        Image(
            painter = painter,
            contentDescription = "Image ${imageDto.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            else -> {}
        }
    }
}

@Composable
fun UploadImageButton(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFolderClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val uploadImageButtonIcon = if (expanded) Icons.Default.Close else Icons.Default.Add
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Column {
            FloatingActionButton(
                onClick = {
                    expanded = true
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = uploadImageButtonIcon,
                    contentDescription = "Add Image"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    onCameraClick()
                    expanded = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                            contentDescription = "Camera"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Camera")
                    }
                }
                DropdownMenuItem(onClick = {
                    onGalleryClick()
                    expanded = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_upload_file_24),
                            contentDescription = "Gallery"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }
                DropdownMenuItem(onClick = {
                    onFolderClick()
                    expanded = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_folder_24),
                            contentDescription = "New album"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Album")
                    }
                }
            }
        }
    }
}