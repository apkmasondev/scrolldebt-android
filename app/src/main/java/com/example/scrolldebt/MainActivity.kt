package com.example.scrolldebt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scrolldebt.ui.screens.MainScreen
import com.example.scrolldebt.ui.screens.MainScreenViewModel
import com.example.scrolldebt.ui.theme.ScrollDebtTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      val viewModel: MainScreenViewModel = hiltViewModel()
      val state by viewModel.state.collectAsStateWithLifecycle()
      val themeMode = state.themeMode

      ScrollDebtTheme(themeMode = themeMode) { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainScreen(viewModel = viewModel) } }
    }
  }
}
