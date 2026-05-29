package dev.stranik.musicapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography

val Purple = Color(0xFF673AB7)
val PurpleBack = Color(0xFF452E73)
val Black = Color(0xFF121212)
val Surface = Color(0xFF1E1E1E)
val SurfaceVar = Color(0xFF282828)
val OnSurface = Color(0xFFFFFFFF)
val OnSurfaceVariant = Color(0xFFB3B3B3)
val Error = Color(0xFFE57373)

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = Color.Black,
    primaryContainer = PurpleBack,
    background = Black,
    surface = Surface,
    surfaceVariant = SurfaceVar,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error
)

private val MusicTypography = Typography(
    headlineLarge = Typography().headlineLarge.copy(color = OnSurface),
    headlineMedium = Typography().headlineMedium.copy(color = OnSurface),
    titleLarge = Typography().titleLarge.copy(color = OnSurface),
    titleMedium = Typography().titleMedium.copy(color = OnSurface),
    bodyLarge = Typography().bodyLarge.copy(color = OnSurface),
    bodyMedium = Typography().bodyMedium.copy(color = OnSurfaceVariant),
    labelSmall = Typography().labelSmall.copy(color = OnSurfaceVariant)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /*val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}