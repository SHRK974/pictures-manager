package com.example.pictsmanager.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pictsmanager.data.mappers.toImagesDtoMap
import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.ImageDto
import com.example.pictsmanager.data.remote.UserDto
import com.example.pictsmanager.domain.util.AppPreferences
import com.example.pictsmanager.domain.util.DateAdapter
import com.example.pictsmanager.presentation.image.ImageCard
import com.example.pictsmanager.presentation.image.ImageViewModel
import com.example.pictsmanager.presentation.ui.theme.Shapes
import java.lang.Integer.max

@Composable
fun AlbumsGrid(
    albumViewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    cardWidth: Dp = AppPreferences.cardWidth.dp,
    cardSpacing: Dp = AppPreferences.cardSpacing.dp
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Section(
            title = "My albums",
            albumViewModel = albumViewModel,
            albums = albumViewModel.state.albums ?: emptyList(),
            cardWidth = cardWidth,
            cardSpacing = cardSpacing
        )
    }
}

@Composable
fun TwoSectionGrid(
    albumViewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    cardWidth: Dp = AppPreferences.cardWidth.dp,
    cardSpacing: Dp = AppPreferences.cardSpacing.dp
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        albumViewModel.state.albumsSharedWithMe?.let {
            Section(
                title = "Albums shared with me",
                albumViewModel = albumViewModel,
                albums = it,
                cardWidth = cardWidth,
                cardSpacing = cardSpacing
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
                .height(3.dp)
                .background(Color.Gray)
        )

        albumViewModel.state.albumSharedWithOther?.let {
            Section(
                title = "Album shared with others",
                albumViewModel = albumViewModel,
                albums = it,
                cardWidth = cardWidth,
                cardSpacing = cardSpacing
            )
        }
    }
}

@Composable
fun Section(
    title: String,
    albumViewModel: AlbumViewModel,
    albums: List<AlbumDto>,
    cardWidth: Dp,
    cardSpacing: Dp
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            color = Color.White
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = cardWidth),
            contentPadding = PaddingValues(all = 10.dp),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            items(albums.size) {index ->
                val album = albums[index]
                AlbumCard(
                    album = album,
                    albumViewModel = albumViewModel,
                    cardWidth = cardWidth,
                    cardSpacing = cardSpacing
                )
            }
        }
    }
}

@Composable
fun AlbumDetailGrid(
    albumViewModel: AlbumViewModel,
    modifier: Modifier = Modifier,
    cardWidth: Dp = AppPreferences.cardWidth.dp,
    cardSpacing: Dp = AppPreferences.cardSpacing.dp
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 10.dp),
        state = listState
    ) {
        albumViewModel.getAllImagesAlbumFromState()?.toImagesDtoMap()?.forEach { pair ->
            val images = pair.second
            val numItemsPerRow = max(1, (screenWidth / (cardWidth + cardSpacing)).toInt())

            item {
                Text(
                    text = DateAdapter.formatDate(pair.first),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Start,
                )
            }
            val windowedImages = images.windowed(
                size = numItemsPerRow,
                step = numItemsPerRow,
                partialWindows = true
            )
            items(windowedImages.size) {index ->
                val rowImage = windowedImages[index]
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowImage.forEach { it ->
                        ImageCard(
                            imageDto = it,
                            albumViewModel = albumViewModel,
                            size = cardWidth,
                            padding = cardSpacing,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(
    modifier: Modifier = Modifier,
    album: AlbumDto,
    albumViewModel: AlbumViewModel,
    cardWidth: Dp,
    cardSpacing: Dp
) {
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf("") }
    val users by rememberUpdatedState(albumViewModel.userQueried)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Share Album") },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it; albumViewModel.searchUsersByEmail(searchQuery) },
                        label = { "Search for a user" },
                        modifier = Modifier.fillMaxWidth()
                    )

                    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                        items(users.size) { index ->
                            val user = users[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = { selectedUser = user.id })
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.email,
                                    modifier = Modifier.weight(1f)
                                )
                                Checkbox(
                                    checked = selectedUser == user.id,
                                    onCheckedChange = {
                                        selectedUser = if (it) {
                                            user.id
                                        } else {
                                            ""
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUser.let {
                            albumViewModel.shareAlbumWith(album.id, it)
                            showDialog = false
                        }
                    }
                ) {
                    Text("Share")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDialog = true
                    }
                )
            }
            .fillMaxSize()
    ) {
        val image = album.images.lastOrNull()
        when (image !== null) {
            true ->
                image?.let {
                    ImageCard(
                        imageDto = image,
                        albumViewModel = albumViewModel,
                        isInteractive = false,
                        size = cardWidth,
                        padding = cardSpacing
                    )
                }
            false ->
                Card(
                    modifier = modifier
                        .size(cardWidth)
                        .padding(all = cardSpacing)
                        .aspectRatio(1f),
                    shape = Shapes.large,
                    elevation = 5.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.surface)
                    ) {
                    }
                }
        }
        
        Column(verticalArrangement = Arrangement.Top) {
            Text(
                text = album.label,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = album.images.size.toString(),
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                lineHeight = 5.sp,
            )
        }
    }

}