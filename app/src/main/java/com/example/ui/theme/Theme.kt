package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PortalBlue,
  onPrimary = Color.White,
  primaryContainer = PortalNavy,
  onPrimaryContainer = PortalBlueLight,
  secondary = SaffronGold,
  onSecondary = Color.Black,
  secondaryContainer = SaffronDark,
  onSecondaryContainer = SaffronLight,
  tertiary = SuccessGreen,
  background = NavyDarkBackground,
  onBackground = DarkTextPrimary,
  surface = NavyDarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = NavyDarkCard,
  onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
  primary = PortalNavy,
  onPrimary = Color.White,
  primaryContainer = PortalBlueLight,
  onPrimaryContainer = PortalNavy,
  secondary = SaffronGold,
  onSecondary = Color.White,
  secondaryContainer = SaffronLight,
  onSecondaryContainer = SaffronDark,
  tertiary = SuccessGreen,
  onTertiary = Color.White,
  tertiaryContainer = SuccessGreenLight,
  onTertiaryContainer = SuccessGreenDark,
  background = BackgroundLight,
  onBackground = TextPrimary,
  surface = SurfaceLight,
  onSurface = TextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondary,
  outline = BorderLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep branded navy & saffron gold palette consistent
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
