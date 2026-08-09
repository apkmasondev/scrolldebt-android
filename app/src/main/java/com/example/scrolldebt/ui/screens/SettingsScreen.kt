package com.example.scrolldebt.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.scrolldebt.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.example.scrolldebt.utils.AppConstants
import com.example.scrolldebt.ui.components.DoomscrollCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    trackedApps: Set<String>,
    installedApps: List<com.example.scrolldebt.utils.AppUsageInfo>,
    onToggleApp: (String) -> Unit,
    thresholdMinutes: Int,
    onThresholdChange: (Int) -> Unit,
    streakThresholdMinutes: Int,
    onStreakThresholdChange: (Int) -> Unit,
    brutalTruthEnabled: Boolean,
    onBrutalTruthToggle: (Boolean) -> Unit,
    pushNotificationsEnabled: Boolean,
    onPushNotificationsToggle: (Boolean) -> Unit,
    trackingMode: com.example.scrolldebt.data.repository.TrackingMode,
    onTrackingModeChange: (com.example.scrolldebt.data.repository.TrackingMode) -> Unit,
    themeMode: Int,
    onThemeModeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Text(
            text = stringResource(R.string.settings_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(28.dp))



        // Monitored Apps Checklist
        Text(
            text = stringResource(R.string.tracked_apps),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.tracked_apps_desc),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val displayedApps = remember(trackedApps, installedApps) {
            val apps = mutableSetOf<com.example.scrolldebt.utils.AppUsageInfo>()
            
            // 1. Show Holy Seven if installed
            AppConstants.DEFAULT_SUGGESTED_APPS.keys.forEach { pkg ->
                val installed = installedApps.find { it.packageName == pkg }
                if (installed != null) {
                    apps.add(installed)
                }
            }
            
            // 2. Show all currently tracked apps
            trackedApps.forEach { pkg ->
                val installed = installedApps.find { it.packageName == pkg }
                if (installed != null) {
                    apps.add(installed)
                }
            }
            
            apps.toList().sortedBy { it.appName }
        }

        var showAppSheet by remember { mutableStateOf(false) }

        displayedApps.forEach { appInfo ->
            val pkg = appInfo.packageName
            val name = appInfo.appName
            val isChecked = trackedApps.contains(pkg)
            DoomscrollCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                backgroundColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant,
                borderColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                onClick = { onToggleApp(pkg) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onToggleApp(pkg) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
                            checkmarkColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { 
                showAppSheet = true 
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.add_other_app_btn),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (showAppSheet) {
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val sheetHeight = configuration.screenHeightDp.dp * 0.8f
            
            ModalBottomSheet(
                onDismissRequest = { showAppSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetHeight)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.select_app_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val unselectedApps = installedApps.filter { !trackedApps.contains(it.packageName) }
                    
                    val overscrollConnection = remember {
                        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
                            override fun onPostScroll(
                                consumed: androidx.compose.ui.geometry.Offset,
                                available: androidx.compose.ui.geometry.Offset,
                                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource
                            ): androidx.compose.ui.geometry.Offset {
                                return if (available.y < 0) available else androidx.compose.ui.geometry.Offset.Zero
                            }
                            override suspend fun onPostFling(
                                consumed: androidx.compose.ui.unit.Velocity,
                                available: androidx.compose.ui.unit.Velocity
                            ): androidx.compose.ui.unit.Velocity {
                                return if (available.y < 0) available else androidx.compose.ui.unit.Velocity.Zero
                            }
                        }
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(overscrollConnection)
                    ) {
                        items(unselectedApps) { app ->
                            DoomscrollCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                borderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                onClick = { 
                                    onToggleApp(app.packageName)
                                    showAppSheet = false
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = app.appName,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Streak Threshold Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.streak_threshold),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$streakThresholdMinutes min",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.streak_threshold_desc),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        var localStreakThreshold by remember(streakThresholdMinutes) { mutableFloatStateOf(streakThresholdMinutes.toFloat()) }
        Slider(
            value = localStreakThreshold,
            onValueChange = { localStreakThreshold = it },
            onValueChangeFinished = { onStreakThresholdChange(localStreakThreshold.toInt()) },
            valueRange = 15f..300f,
            steps = 284,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).semantics { 
                contentDescription = "Streak threshold slider" 
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Push Notifications Switch
        val context = androidx.compose.ui.platform.LocalContext.current
        val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    onPushNotificationsToggle(true)
                } else {
                    onPushNotificationsToggle(false)
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.push_notifications_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.push_notifications_desc),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Switch(
                modifier = Modifier.semantics { contentDescription = "Push notifications toggle" },
                checked = pushNotificationsEnabled,
                onCheckedChange = { enable ->
                    if (enable && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            onPushNotificationsToggle(true)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        onPushNotificationsToggle(enable)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        AnimatedVisibility(visible = pushNotificationsEnabled) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.push_notifications_label),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$thresholdMinutes min",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                var localThreshold by remember(thresholdMinutes) { mutableFloatStateOf(thresholdMinutes.toFloat()) }
                Slider(
                    value = localThreshold,
                    onValueChange = { localThreshold = it },
                    onValueChangeFinished = { onThresholdChange(localThreshold.toInt()) },
                    valueRange = 15f..300f,
                    steps = 284,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onBackground,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))
                
                Text(
                    text = stringResource(R.string.tracking_mode_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tracking_mode_desc),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                DoomscrollCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    backgroundColor = if (trackingMode == com.example.scrolldebt.data.repository.TrackingMode.REALTIME) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = if (trackingMode == com.example.scrolldebt.data.repository.TrackingMode.REALTIME) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                    onClick = { 
                        onTrackingModeChange(com.example.scrolldebt.data.repository.TrackingMode.REALTIME) 
                    },
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.RadioButton(
                                selected = trackingMode == com.example.scrolldebt.data.repository.TrackingMode.REALTIME,
                                onClick = { 
                                    onTrackingModeChange(com.example.scrolldebt.data.repository.TrackingMode.REALTIME)
                                },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = MaterialTheme.colorScheme.tertiary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.sniper_mode_title),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = stringResource(R.string.sniper_mode_desc),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 48.dp)
                        )
                    }
                }
                
                DoomscrollCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (trackingMode == com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = if (trackingMode == com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                    onClick = { onTrackingModeChange(com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) },
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.RadioButton(
                                selected = trackingMode == com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER,
                                onClick = { onTrackingModeChange(com.example.scrolldebt.data.repository.TrackingMode.BATTERY_SAVER) },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary, unselectedColor = MaterialTheme.colorScheme.tertiary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.battery_saver_title),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = stringResource(R.string.battery_saver_desc),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 48.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Brutal Truth Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.brutal_truth_label),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.brutal_truth_desc),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Switch(
                modifier = Modifier.semantics { contentDescription = "Brutal truth toggle" },
                checked = brutalTruthEnabled,
                onCheckedChange = onBrutalTruthToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Theme Selection (Bottom)
        Text(
            text = stringResource(R.string.appearance_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val lightSelected = themeMode == 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (lightSelected) MaterialTheme.colorScheme.background else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onThemeModeChange(1) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.theme_light),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = if (lightSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            val darkSelected = themeMode == 2
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (darkSelected) MaterialTheme.colorScheme.background else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onThemeModeChange(2) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.theme_dark),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = if (darkSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

    }
}
