package com.example.scrolldebt.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = AccentRed,
  secondary = StarkWhite,
  tertiary = LightGray,
  background = PureBlack,
  surface = PureBlack,
  surfaceVariant = DarkGray,
  onPrimary = StarkWhite,
  onSecondary = PureBlack,
  onTertiary = StarkWhite,
  onBackground = StarkWhite,
  onSurface = StarkWhite,
  onSurfaceVariant = StarkWhite
)

private val LightColorScheme = lightColorScheme(
  primary = AccentRed,
  secondary = PureBlack,
  tertiary = DarkerGray,
  background = StarkWhite,
  surface = StarkWhite,
  surfaceVariant = LightSurface,
  onPrimary = StarkWhite,
  onSecondary = StarkWhite,
  onTertiary = PureBlack,
  onBackground = PureBlack,
  onSurface = PureBlack,
  onSurfaceVariant = PureBlack
)

@Composable
fun ScrollDebtTheme(
  themeMode: Int = 2, // 1 = Light, 2 = Dark
  content: @Composable () -> Unit,
) {
  val darkTheme = when (themeMode) {
      1 -> false
      else -> true
  }
  
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  
  val view = LocalView.current
  if (!view.isInEditMode) {
      SideEffect {
          var context = view.context
          while (context is android.content.ContextWrapper) {
              if (context is Activity) break
              context = context.baseContext
          }
          val window = (context as? Activity)?.window
          if (window != null) {
              WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
              WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
          }
      }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
