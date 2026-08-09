package com.example.scrolldebt

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
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

    enableEdgeToEdge()
    setContent {
      val viewModel: MainScreenViewModel = hiltViewModel()
      val state by viewModel.state.collectAsStateWithLifecycle()

      ScrollDebtTheme(themeMode = state.themeMode) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainScreen(viewModel = viewModel)
        }
      }
    }
  }
}
