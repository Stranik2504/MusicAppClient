package dev.stranik.musicapp.domain

import dev.stranik.musicapp.domain.usecase.CreatePlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetFeaturedAlbumsUseCase
import dev.stranik.musicapp.domain.usecase.GetLikedTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetNewReleasesUseCase
import dev.stranik.musicapp.domain.usecase.GetPopularArtistsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecentlyPlayedUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.ObservePlayerStateUseCase
import dev.stranik.musicapp.domain.usecase.PauseTrackUseCase
import dev.stranik.musicapp.domain.usecase.PlayTrackUseCase
import dev.stranik.musicapp.domain.usecase.SearchUseCase
import dev.stranik.musicapp.domain.usecase.SeekUseCase
import dev.stranik.musicapp.domain.usecase.SkipNextUseCase
import dev.stranik.musicapp.domain.usecase.SkipPreviousUseCase

object Creator {
    fun provideCreatePlaylist(): CreatePlaylistUseCase = CreatePlaylistUseCase()
    fun provideGetFeaturedAlbums(): GetFeaturedAlbumsUseCase = GetFeaturedAlbumsUseCase()
    fun provideGetLikedTracks(): GetLikedTracksUseCase = GetLikedTracksUseCase()
    fun provideGetNewReleases(): GetNewReleasesUseCase = GetNewReleasesUseCase()
    fun provideGetPopularArtists(): GetPopularArtistsUseCase = GetPopularArtistsUseCase()
    fun provideGetRecentlyPlayed(): GetRecentlyPlayedUseCase = GetRecentlyPlayedUseCase()
    fun provideGetUserPlaylists(): GetUserPlaylistsUseCase = GetUserPlaylistsUseCase()
    fun provideLikeTrack(): LikeTrackUseCase = LikeTrackUseCase()
    fun provideObservePlayerState(): ObservePlayerStateUseCase = ObservePlayerStateUseCase()
    fun providePauseTrack(): PauseTrackUseCase = PauseTrackUseCase()
    fun providePlayTrack(): PlayTrackUseCase = PlayTrackUseCase()
    fun provideSearch(): SearchUseCase = SearchUseCase()
    fun provideSeek(): SeekUseCase = SeekUseCase()
    fun provideSkipNext(): SkipNextUseCase = SkipNextUseCase()
    fun provideSkipPrevious(): SkipPreviousUseCase = SkipPreviousUseCase()
}