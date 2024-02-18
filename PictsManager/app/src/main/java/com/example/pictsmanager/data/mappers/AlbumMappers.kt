package com.example.pictsmanager.data.mappers

import com.example.pictsmanager.data.remote.AlbumDto
import com.example.pictsmanager.data.remote.ImageDto
import java.time.LocalDate
import java.time.ZoneId
fun AlbumDto.toImagesDtoMap(): List<Pair<LocalDate, List<ImageDto>>> {
    val imagesByDate = images.groupBy { imageDto ->
        imageDto.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate()
    }
    return imagesByDate.entries
        .map { entry -> Pair(entry.key, entry.value.reversed()) }
        .sortedByDescending { pair -> pair.first }
}