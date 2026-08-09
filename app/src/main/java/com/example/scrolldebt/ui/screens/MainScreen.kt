package com.example.scrolldebt.ui.screens
import com.example.scrolldebt.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import com.example.scrolldebt.ui.screens.LifeLostScreen
import com.example.scrolldebt.ui.screens.OnboardingScreen
import com.example.scrolldebt.ui.screens.SettingsScreen
import com.example.scrolldebt.ui.screens.TodayScreen
import com.example.scrolldebt.ui.theme.ScrollDebtTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Automatically check for permissions and refresh stats when app is resumed
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionAndRefresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (!state.hasPermission) {
        OnboardingScreen(modifier = modifier)
    } else {
        var selectedTab by androidx.compose.runtime.saveable.rememberSaveable { mutableIntStateOf(0) }
        var showLanguageMenu by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Change Language",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            val languages = listOf("EN" to "en", "ES" to "es", "FR" to "fr", "DE" to "de", "PL" to "pl")
                            languages.forEach { (label, code) ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = label, 
                                            color = if (state.language.lowercase() == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (state.language.lowercase() == code) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    onClick = {
                                        showLanguageMenu = false
                                        if (state.language.lowercase() != code) {
                                            viewModel.changeLanguage(code)
                                            // Resources are bound to the Activity's base context, so the
                                            // new language only takes effect after the Activity is rebuilt.
                                            // The ViewModel is retained across this, so nothing is reloaded.
                                            (context as? android.app.Activity)?.recreate()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        stringResource(R.string.tab_today),
                        stringResource(R.string.tab_lost),
                        stringResource(R.string.tab_settings)
                    )
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTab) {
                    0 -> TodayScreen(
                        totalTimeMs = state.todayTotalTimeMs,
                        streakDays = state.currentStreakDays,
                        appBreakdown = state.appBreakdown,
                        brutalTruthMessage = state.brutalTruthMessage,
                        brutalTruthEnabled = state.brutalTruthEnabled,
                        onRefreshRoast = { viewModel.refreshRoastMessage() }
                    )
                    1 -> LifeLostScreen(
                        todayTimeMs = state.todayTotalTimeMs,
                        weeklyTimeMs = state.weeklyTotalTimeMs,
                        historicalRecords = state.historicalRecords,
                        language = state.language
                    )
                    2 -> SettingsScreen(
                        trackedApps = state.trackedApps,
                        installedApps = state.installedApps,
                        onToggleApp = { viewModel.toggleTrackedApp(it) },
                        thresholdMinutes = state.thresholdMinutes,
                        onThresholdChange = { viewModel.setThreshold(it) },
                        streakThresholdMinutes = state.streakThresholdMinutes,
                        onStreakThresholdChange = { viewModel.setStreakThreshold(it) },
                        brutalTruthEnabled = state.brutalTruthEnabled,
                        onBrutalTruthToggle = { viewModel.toggleBrutalTruth(it) },
                        pushNotificationsEnabled = state.pushNotificationsEnabled,
                        onPushNotificationsToggle = { viewModel.togglePushNotifications(it) },
                        trackingMode = state.trackingMode,
                        onTrackingModeChange = { viewModel.setTrackingMode(it) },
                        themeMode = state.themeMode,
                        onThemeModeChange = { viewModel.setThemeMode(it) }
                    )
                }
            }
        }
    }
}
