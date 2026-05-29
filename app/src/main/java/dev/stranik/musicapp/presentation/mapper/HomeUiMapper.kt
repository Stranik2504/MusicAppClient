package dev.stranik.musicapp.presentation.mapper

import dev.stranik.musicapp.domain.model.Album
import dev.stranik.musicapp.domain.model.Artist
import dev.stranik.musicapp.domain.model.Track

class HomeUiMapper {
    
    fun toAlbum(album: Album): Album {
        // Для данного примера просто возвращаем данные как есть
        // В реальном приложении здесь могла бы быть более сложная трансформация
        return album
    }
    
    fun toTrack(track: Track): Track {
        // Для данного примера просто возвращаем данные как есть
        // В реальном приложении здесь могла бы быть более сложная трансформация
        return track
    }
    
    fun toArtist(artist: Artist): Artist {
        // Для данного примера просто возвращаем данные как есть
        // В реальном приложении здесь могла бы быть более сложная трансформация
        return artist
    }
}

