package com.example.scrolldebt.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.scrolldebt.MainActivity
import com.example.scrolldebt.R
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.utils.Localization
import com.example.scrolldebt.utils.UsageStatsHelper

class DoomClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = PreferencesManager(context)

        val totalTimeMs = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val helper = UsageStatsHelper(context)
            if (helper.hasUsageStatsPermission()) {
                val stats = helper.getTodayUsageStats()
                stats.sumOf { it.timeSpentMs }
            } else {
                0L
            }
        }

        val totalHours = totalTimeMs / 1000.0 / 60.0 / 60.0
        val timeString = String.format(java.util.Locale.US, "%.1f", totalHours)
        val language = prefs.getLanguage()

        provideContent {
            WidgetContent(timeString, language)
        }
    }

    @Composable
    private fun WidgetContent(timeString: String, language: String) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_background))
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Header
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                Text(
                    text = "DOOM",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFF3333)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "SCROLL",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFAAAAAA)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.height(16.dp))
            
            // Time
            Row(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text(
                    text = timeString,
                    maxLines = 1,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    text = "h",
                    maxLines = 1,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFF3333)),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.height(16.dp))
            
            // Divider
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x33FFFFFF))
            ) {}
            
            Spacer(modifier = GlanceModifier.height(12.dp))
            
            // Footer Text
            Text(
                text = androidx.glance.LocalContext.current.getString(R.string.widget_wasted_time),
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFF8888)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = androidx.glance.text.TextAlign.Center
                )
            )
        }
    }
}

class DoomClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DoomClockWidget()
}
