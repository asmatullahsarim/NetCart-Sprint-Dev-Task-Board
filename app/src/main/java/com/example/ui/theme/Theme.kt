package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class DevSprintExtendedColors(
  val background: Color,
  val surfaceCard: Color,
  val surfaceElevated: Color,
  val borderSubtle: Color,
  val accentCyan: Color,
  val accentCyanContainer: Color,
  val successEmerald: Color,
  val successContainer: Color,
  val warningAmber: Color,
  val warningContainer: Color,
  val criticalCrimson: Color,
  val criticalContainer: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val isDark: Boolean,
)

val DarkExtendedColors =
  DevSprintExtendedColors(
    background = MidnightNavy,
    surfaceCard = SlateDark,
    surfaceElevated = SlateElevated,
    borderSubtle = SlateBorder,
    accentCyan = ElectricCyan,
    accentCyanContainer = ElectricCyanDark,
    successEmerald = EmeraldGreen,
    successContainer = EmeraldDark,
    warningAmber = AmberGold,
    warningContainer = AmberDark,
    criticalCrimson = CrimsonRed,
    criticalContainer = CrimsonDark,
    textPrimary = CrispWhite,
    textSecondary = MutedSlate,
    isDark = true,
  )

val LightExtendedColors =
  DevSprintExtendedColors(
    background = LightBackground,
    surfaceCard = LightSurface,
    surfaceElevated = LightElevated,
    borderSubtle = LightBorder,
    accentCyan = LightCyanAccent,
    accentCyanContainer = LightCyanContainer,
    successEmerald = LightEmerald,
    successContainer = LightEmeraldContainer,
    warningAmber = LightAmber,
    warningContainer = LightAmberContainer,
    criticalCrimson = LightCrimson,
    criticalContainer = LightCrimsonContainer,
    textPrimary = LightPrimaryText,
    textSecondary = LightSecondaryText,
    isDark = false,
  )

val LocalDevSprintColors = staticCompositionLocalOf { DarkExtendedColors }

private val DarkColorScheme =
  darkColorScheme(
    primary = ElectricCyan,
    onPrimary = MidnightNavy,
    primaryContainer = ElectricCyanDark,
    onPrimaryContainer = ElectricCyan,
    secondary = EmeraldGreen,
    onSecondary = MidnightNavy,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = EmeraldGreen,
    tertiary = AmberGold,
    onTertiary = MidnightNavy,
    tertiaryContainer = AmberDark,
    onTertiaryContainer = AmberGold,
    error = CrimsonRed,
    onError = CrispWhite,
    errorContainer = CrimsonDark,
    onErrorContainer = CrimsonRed,
    background = MidnightNavy,
    onBackground = CrispWhite,
    surface = SlateDark,
    onSurface = CrispWhite,
    surfaceVariant = SlateElevated,
    onSurfaceVariant = MutedSlate,
    outline = SlateBorder,
    outlineVariant = SlateBorder,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LightCyanAccent,
    onPrimary = Color.White,
    primaryContainer = LightCyanContainer,
    onPrimaryContainer = LightPrimaryText,
    secondary = LightEmerald,
    onSecondary = Color.White,
    secondaryContainer = LightEmeraldContainer,
    onSecondaryContainer = LightPrimaryText,
    tertiary = LightAmber,
    onTertiary = Color.White,
    tertiaryContainer = LightAmberContainer,
    onTertiaryContainer = LightPrimaryText,
    error = LightCrimson,
    onError = Color.White,
    errorContainer = LightCrimsonContainer,
    onErrorContainer = LightCrimson,
    background = LightBackground,
    onBackground = LightPrimaryText,
    surface = LightSurface,
    onSurface = LightPrimaryText,
    surfaceVariant = LightElevated,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    outlineVariant = LightBorder,
  )

// Card Corner Radius: 14dp, Button Corner Radius: 10dp
val DevSprintShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
  )

object DevSprintTheme {
  val colors: DevSprintExtendedColors
    @Composable get() = LocalDevSprintColors.current
}

@Composable
fun DevSprintTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

  CompositionLocalProvider(LocalDevSprintColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = DevSprintTypography,
      shapes = DevSprintShapes,
      content = content,
    )
  }
}
