package dev.stranik.musicapp.domain

import android.content.Context
import dev.stranik.musicapp.data.local.TokenManager
import dev.stranik.musicapp.data.repository.AlbumRepositoryImpl
import dev.stranik.musicapp.data.repository.ArtistRepositoryImpl
import dev.stranik.musicapp.data.repository.AuthRepositoryImpl
import dev.stranik.musicapp.data.repository.LibraryRepositoryImpl
import dev.stranik.musicapp.data.repository.PlayerRepositoryImpl
import dev.stranik.musicapp.data.repository.RecommendationRepositoryImpl
import dev.stranik.musicapp.data.repository.SearchRepositoryImpl
import dev.stranik.musicapp.data.repository.TrackRepositoryImpl
import dev.stranik.musicapp.data.repository.UserRepositoryImpl
import dev.stranik.musicapp.domain.repository.AlbumRepository
import dev.stranik.musicapp.domain.repository.ArtistRepository
import dev.stranik.musicapp.domain.repository.AuthRepository
import dev.stranik.musicapp.domain.repository.LibraryRepository
import dev.stranik.musicapp.domain.repository.PlayerRepository
import dev.stranik.musicapp.domain.repository.RecommendationRepository
import dev.stranik.musicapp.domain.repository.SearchRepository
import dev.stranik.musicapp.domain.repository.TrackRepository
import dev.stranik.musicapp.domain.repository.UserRepository
import dev.stranik.musicapp.domain.usecase.AddTrackToPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.CreatePlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetAlbumUseCase
import dev.stranik.musicapp.domain.usecase.GetArtistUseCase
import dev.stranik.musicapp.domain.usecase.GetFeaturedAlbumsUseCase
import dev.stranik.musicapp.domain.usecase.GetLikedTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetMeUseCase
import dev.stranik.musicapp.domain.usecase.GetPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.GetRecommendationTracksUseCase
import dev.stranik.musicapp.domain.usecase.GetPopularArtistsUseCase
import dev.stranik.musicapp.domain.usecase.GetRecentlyPlayedUseCase
import dev.stranik.musicapp.domain.usecase.GetTrackUseCase
import dev.stranik.musicapp.domain.usecase.GetUserPlaylistsUseCase
import dev.stranik.musicapp.domain.usecase.IsLoggedInUseCase
import dev.stranik.musicapp.domain.usecase.LikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.LoginUseCase
import dev.stranik.musicapp.domain.usecase.LogoutUseCase
import dev.stranik.musicapp.domain.usecase.ObservePlayerStateUseCase
import dev.stranik.musicapp.domain.usecase.PauseTrackUseCase
import dev.stranik.musicapp.domain.usecase.PlayPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.PlayTrackUseCase
import dev.stranik.musicapp.domain.usecase.RegistrationUseCase
import dev.stranik.musicapp.domain.usecase.RemoveTrackFromPlaylistUseCase
import dev.stranik.musicapp.domain.usecase.SearchUseCase
import dev.stranik.musicapp.domain.usecase.SeekUseCase
import dev.stranik.musicapp.domain.usecase.SetRepeatModeUseCase
import dev.stranik.musicapp.domain.usecase.SetShuffleUseCase
import dev.stranik.musicapp.domain.usecase.SkipNextUseCase
import dev.stranik.musicapp.domain.usecase.SkipPreviousUseCase
import dev.stranik.musicapp.domain.usecase.UnlikeTrackUseCase
import dev.stranik.musicapp.domain.usecase.UpdatePlayerLikeStatusUseCase

object Creator {
    private var playerRepository: PlayerRepository? = null

    fun provideTokenManager(context: Context): TokenManager = TokenManager(context)

    fun provideAuthRepository(tokenManager: TokenManager): AuthRepository = AuthRepositoryImpl(tokenManager)
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()
    fun provideLibraryRepository(): LibraryRepository = LibraryRepositoryImpl()
    fun provideTrackRepository(): TrackRepository = TrackRepositoryImpl()
    fun provideRecommendationRepository(): RecommendationRepository = RecommendationRepositoryImpl()
    fun provideAlbumRepository(): AlbumRepository = AlbumRepositoryImpl()
    fun provideSearchRepository(): SearchRepository = SearchRepositoryImpl()
    fun provideArtistRepository(): ArtistRepository = ArtistRepositoryImpl()
    
    fun providePlayerRepository(context: Context): PlayerRepository {
        return playerRepository ?: PlayerRepositoryImpl(context).also {
            playerRepository = it
        }
    }

    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase = LoginUseCase(authRepository)
    fun provideRegistrationUseCase(authRepository: AuthRepository): RegistrationUseCase = RegistrationUseCase(authRepository)
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase = LogoutUseCase(authRepository)
    fun provideIsLoggedInUseCase(authRepository: AuthRepository): IsLoggedInUseCase = IsLoggedInUseCase(authRepository)
    fun provideGetMe(userRepository: UserRepository): GetMeUseCase = GetMeUseCase(userRepository)
    fun provideGetPlaylistUseCase(libraryRepository: LibraryRepository): GetPlaylistUseCase = GetPlaylistUseCase(libraryRepository)

    fun provideCreatePlaylist(libraryRepository: LibraryRepository): CreatePlaylistUseCase = CreatePlaylistUseCase(libraryRepository)
    fun provideGetLikedTracks(libraryRepository: LibraryRepository): GetLikedTracksUseCase = GetLikedTracksUseCase(libraryRepository)
    fun provideGetFeaturedAlbums(recommendationRepository: RecommendationRepository): GetFeaturedAlbumsUseCase = GetFeaturedAlbumsUseCase(recommendationRepository)
    fun provideGetNewReleases(recommendationRepository: RecommendationRepository): GetRecommendationTracksUseCase = GetRecommendationTracksUseCase(recommendationRepository)
    fun provideGetPopularArtists(recommendationRepository: RecommendationRepository): GetPopularArtistsUseCase = GetPopularArtistsUseCase(recommendationRepository)
    fun provideGetRecentlyPlayed(recommendationRepository: RecommendationRepository): GetRecentlyPlayedUseCase = GetRecentlyPlayedUseCase(recommendationRepository)
    fun provideGetUserPlaylists(libraryRepository: LibraryRepository): GetUserPlaylistsUseCase = GetUserPlaylistsUseCase(libraryRepository)
    
    fun provideLikeTrack(trackRepository: TrackRepository): LikeTrackUseCase = LikeTrackUseCase(trackRepository)
    fun provideUnlikeTrack(trackRepository: TrackRepository): UnlikeTrackUseCase = UnlikeTrackUseCase(trackRepository)
    
    fun provideAddTrackToPlaylist(libraryRepository: LibraryRepository): AddTrackToPlaylistUseCase = AddTrackToPlaylistUseCase(libraryRepository)
    fun provideRemoveTrackFromPlaylist(libraryRepository: LibraryRepository): RemoveTrackFromPlaylistUseCase = RemoveTrackFromPlaylistUseCase(libraryRepository)

    fun provideGetAlbum(albumRepository: AlbumRepository): GetAlbumUseCase = GetAlbumUseCase(albumRepository)
    fun provideGetArtist(artistRepository: ArtistRepository): GetArtistUseCase = GetArtistUseCase(artistRepository)
    fun provideGetTrack(trackRepository: TrackRepository): GetTrackUseCase = GetTrackUseCase(trackRepository)

    fun provideObservePlayerState(playerRepository: PlayerRepository): ObservePlayerStateUseCase = ObservePlayerStateUseCase(playerRepository)
    fun providePauseTrack(playerRepository: PlayerRepository): PauseTrackUseCase = PauseTrackUseCase(playerRepository)
    fun providePlayTrack(playerRepository: PlayerRepository, trackRepository: TrackRepository): PlayTrackUseCase = PlayTrackUseCase(playerRepository, trackRepository)
    fun providePlayPlaylistUseCase(playerRepository: PlayerRepository, trackRepository: TrackRepository): PlayPlaylistUseCase = PlayPlaylistUseCase(playerRepository, trackRepository)
    fun provideSearch(searchRepository: SearchRepository): SearchUseCase = SearchUseCase(searchRepository)
    fun provideSeek(playerRepository: PlayerRepository): SeekUseCase = SeekUseCase(playerRepository)
    fun provideSkipNext(playerRepository: PlayerRepository): SkipNextUseCase = SkipNextUseCase(playerRepository)
    fun provideSkipPrevious(playerRepository: PlayerRepository): SkipPreviousUseCase = SkipPreviousUseCase(playerRepository)

    fun provideSetRepeatMode(playerRepository: PlayerRepository): SetRepeatModeUseCase = SetRepeatModeUseCase(playerRepository)
    fun provideSetShuffle(playerRepository: PlayerRepository): SetShuffleUseCase = SetShuffleUseCase(playerRepository)
    fun provideUpdatePlayerLikeStatus(playerRepository: PlayerRepository): UpdatePlayerLikeStatusUseCase = UpdatePlayerLikeStatusUseCase(playerRepository)
}
