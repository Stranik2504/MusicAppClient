package dev.stranik.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.stranik.musicapp.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val email: String,
    val avatarUrl: String?,
    val isMe: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)

fun UserEntity.toDomain() = User(
    username = username,
    email = email,
    avatarUrl = avatarUrl
)

fun User.toEntity(isMe: Boolean = false) = UserEntity(
    username = username,
    email = email,
    avatarUrl = avatarUrl,
    isMe = isMe
)
