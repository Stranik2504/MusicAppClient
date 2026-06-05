package dev.stranik.musicapp.domain

import android.content.Context
import dev.stranik.musicapp.data.local.TokenManager
import dev.stranik.musicapp.data.repository.AuthRepositoryImpl
import dev.stranik.musicapp.data.repository.UserRepositoryImpl
import dev.stranik.musicapp.domain.repository.AuthRepository
import dev.stranik.musicapp.domain.repository.UserRepository
import dev.stranik.musicapp.domain.usecase.CreatePlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetFeaturedAlbumsUseCase
import dev.stranik.musicapp.domain.usecase.GetLikedTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetMeUseCase
import dev.stranik.musicapp.domain.usecase.GetNewReleasesUseCase
import dev.stranik.musicapp.domain.usecase.GetPopularArtistsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecentlyPlayedUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.IsLoggedInUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.LoginUseCase
import dev.stranik.musicapp.domain.usecase.LogoutUseCase
import dev.stranik.musicapp.domain.usecase.ObservePlayerStateUseCase
import dev.stranik.musicapp.domain.usecase.PauseTrackUseCase
import dev.stranik.musicapp.domain.usecase.PlayTrackUseCase
import dev.stranik.musicapp.domain.usecase.RegistrationUseCase
import dev.stranik.musicapp.domain.usecase.SearchUseCase
import dev.stranik.musicapp.domain.usecase.SeekUseCase
import dev.stranik.musicapp.domain.usecase.SkipNextUseCase
import dev.stranik.musicapp.domain.usecase.SkipPreviousUseCase

object Creator {
    fun provideTokenManager(context: Context): TokenManager = TokenManager(context)

    fun provideAuthRepository(tokenManager: TokenManager): AuthRepository = AuthRepositoryImpl(tokenManager)
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase = LoginUseCase(authRepository)
    fun provideRegistrationUseCase(authRepository: AuthRepository): RegistrationUseCase = RegistrationUseCase(authRepository)
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase = LogoutUseCase(authRepository)
    fun provideIsLoggedInUseCase(authRepository: AuthRepository): IsLoggedInUseCase = IsLoggedInUseCase(authRepository)
    fun provideGetMe(userRepository: UserRepository): GetMeUseCase = GetMeUseCase(userRepository)

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