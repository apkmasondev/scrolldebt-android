package com.example.scrolldebt

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.ui.screens.MainScreen
import com.example.scrolldebt.ui.screens.MainScreenViewModel
import com.example.scrolldebt.ui.theme.ScrollDebtTheme
import com.example.scrolldebt.utils.LocaleUtils

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  /**
   * Rebases the whole Activity on the user's chosen language before any resource is read.
   * Doing it here (rather than wrapping individual composables) means `stringResource`,
   * dialogs and system-rendered text all agree on one language.
   *
   * [MainScreenViewModel.changeLanguage] triggers `recreate()`, which re-enters this method
   * and picks up the new preference. The ViewModel survives that recreation, so no state is lost.
   */
  override fun attachBaseContext(newBase: Context) {
    val language = PreferencesManager(newBase).getLanguage()
    super.attachBaseContext(LocaleUtils.withAppLocale(newBase, language))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Tie the system bars to the app's own theme setting rather than the device's.
    // enableEdgeToEdge() with no arguments derives the scrim from the *system* night mode,
    // which is unrelated to the in-app Light/Dark switch - so on a light-themed device the
    // status and navigation bars rendered light over the app's dark background.
    val isDarkTheme = PreferencesManager(this).getThemeMode() != THEME_MODE_LIGHT
    val barStyle = if (isDarkTheme) {
      SystemBarStyle.dark(Color.TRANSPARENT)
    } else {
      SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    }
    enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)

    setContent {
      val viewModel: MainScreenViewModel = hiltViewModel()
      val state by viewModel.state.collectAsStateWithLifecycle()

      // Keep the system bars in step when the user flips Light/Dark at runtime; onCreate
      // only runs once, so without this the bars keep the theme the app started with.
      LaunchedEffect(state.themeMode) {
        val dark = state.themeMode != THEME_MODE_LIGHT
        WindowCompat.getInsetsController(window, window.decorView)
          .isAppearanceLightStatusBars = !dark
        WindowCompat.getInsetsController(window, window.decorView)
          .isAppearanceLightNavigationBars = !dark
      }

      ScrollDebtTheme(themeMode = state.themeMode) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainScreen(viewModel = viewModel)
        }
      }
    }
  }

  companion object {
    /** Matches PreferencesManager's encoding: 1 = Light, 2 = Dark. */
    private const val THEME_MODE_LIGHT = 1
  }
}
