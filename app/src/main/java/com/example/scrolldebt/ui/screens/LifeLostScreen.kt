package com.example.scrolldebt.ui.screens

import androidx.compose.foundation.background
import com.example.scrolldebt.domain.usecases.BrutalTruthEngine
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.scrolldebt.R
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scrolldebt.utils.ShareUtils
import com.example.scrolldebt.utils.TimeFormatUtils
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.scrolldebt.data.models.UsageRecord

@Composable
fun LifeLostScreen(
    todayTimeMs: Long,
    weeklyTimeMs: Long,
    historicalRecords: List<UsageRecord>,
    language: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val todayDateStr = java.time.LocalDate.now().toString()
    val pastRecords = historicalRecords.filter { it.date != todayDateStr }

    // Aggregate stats
    val historicalTotalMs = pastRecords.sumOf { it.totalTimeMs }
    val totalTimeMs = todayTimeMs + historicalTotalMs

    val totalHours = totalTimeMs.toDouble() / 1000 / 60 / 60

    // Conversions
    val lostDays = totalHours / 24
    val sleepCycles = totalHours / 1.5
    val booksRead = totalHours / 4.0 // approx 4 hours per average book
    val moviesWatched = totalHours / 2.0 // average 2 hour movie
    val workoutsMissed = totalHours / 1.5 // 1.5 hours per gym session
    val marathonsRun = totalHours / 4.5 // average marathon time
    val newSkills = totalHours / 100.0 // 100 hours for intermediate skill

    val moneyRate = when (language.lowercase()) {
        "pl" -> 30.0
        "en" -> 15.0
        "es" -> 8.0
        "fr" -> 11.0
        "de" -> 12.0
        else -> 15.0
    }
    val moneySymbol = when (language.lowercase()) {
        "pl" -> "zł"
        "en" -> "$"
        "es" -> "€"
        "fr" -> "€"
        "de" -> "€"
        else -> "$"
    }
    val wastedMoney = totalHours * moneyRate

    // Weekly aggregate (past 7 days including today)
    val pastWeekRecords = pastRecords.take(6) // past 6 days from DB (excluding today)
    val weekTotalMs = todayTimeMs + pastWeekRecords.sumOf { it.totalTimeMs }
    val weekHours = weekTotalMs.toDouble() / 1000 / 60 / 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Text(
            text = stringResource(R.string.life_lost_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 3.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.life_lost_subtitle),
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 10.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Total Wasted Hours card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.total_wasted),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = run {
                        val totalMin = (totalTimeMs / 1000 / 60)
                        TimeFormatUtils.formatSmartTime(context, totalMin)
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = run {
                        val weekMin = (weekTotalMs / 1000 / 60)
                        val formattedTime = TimeFormatUtils.formatSmartTime(context, weekMin)
                        val prefix = when (language.lowercase()) {
                            "en" -> "This week:"
                            "es" -> "Esta semana:"
                            "fr" -> "Cette semaine :"
                            "de" -> "Diese Woche:"
                            else -> "W tym tygodniu:"
                        }
                        "$prefix $formattedTime"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Chart Section
        if (pastWeekRecords.isNotEmpty() || todayTimeMs > 0) {
            Text(
                text = stringResource(R.string.weekly_chart),
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Get past 7 dates including today
            val today = java.time.LocalDate.now()
            val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }
            
            // Map records by date string "YYYY-MM-DD"
            val recordsMap = historicalRecords.associateBy { it.date }
            
            val chartData = last7Days.map { date ->
                val ms = if (date == today) todayTimeMs else recordsMap[date.toString()]?.totalTimeMs ?: 0L
                val label = if (date == today) {
                    when (language.lowercase()) {
                        "pl" -> "DZIŚ"
                        "es" -> "HOY"
                        "fr" -> "AUJ."
                        "de" -> "HEUTE"
                        else -> "TODAY"
                    }
                } else {
                    date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale(language)).uppercase()
                }
                Pair(label, ms.toFloat())
            }
            
            val maxVal = chartData.maxOf { it.second }.coerceAtLeast(1f)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Value label above bar
                        val totalBarMin = (value / 1000f / 60f).toInt()
                        val barH = totalBarMin / 60
                        val barM = totalBarMin % 60
                        if (value > 0f) {
                            Text(
                                text = if (barH > 0) String.format("%dh %02dm", barH, barM) else String.format("%dm", barM),
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = " ", // Empty space for alignment
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // The bar
                        val heightFraction = (value / maxVal)
                        val actualFraction = if (value > 0f) heightFraction.coerceAtLeast(0.05f) else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(actualFraction)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Day label below bar
                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Wasted Money Highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.wasted_money),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.wasted_money_desc),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                
                // Formatted Currency
                val formattedMoney = when {
                    wastedMoney >= 1_000_000 -> String.format(java.util.Locale.US, "%.2fM", wastedMoney / 1_000_000)
                    wastedMoney >= 10_000 -> String.format(java.util.Locale.US, "%.1fK", wastedMoney / 1_000)
                    else -> String.format(java.util.Locale.US, "%.2f", wastedMoney)
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    if (moneySymbol == "$") {
                        Text(
                            text = "$",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                        )
                    }
                    Text(
                        text = formattedMoney,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (moneySymbol != "$") {
                        Text(
                            text = moneySymbol,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp, start = 6.dp)
                        )
                    }
                }
            }
        }

        // Equivalents Section
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.could_instead),
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        fun formatEquivalent(value: Double): String {
            return when {
                value >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", value / 1_000_000)
                value >= 10_000 -> String.format(java.util.Locale.US, "%.1fK", value / 1_000)
                else -> String.format(java.util.Locale.US, "%.1f", value)
            }
        }

        // Conversion grid lists
        val equivalents = listOf(
            Triple(
                stringResource(R.string.stracone_dni),
                formatEquivalent(lostDays),
                stringResource(R.string.lost_days_desc)
            ),
            Triple(
                stringResource(R.string.cykle_snu),
                formatEquivalent(sleepCycles),
                stringResource(R.string.sleep_cycles_desc)
            ),
            Triple(
                stringResource(R.string.opuszczone_treningi),
                formatEquivalent(workoutsMissed),
                stringResource(R.string.workouts_missed_desc)
            ),
            Triple(
                stringResource(R.string.przeczytane_ksiazki),
                formatEquivalent(booksRead),
                stringResource(R.string.books_read_desc)
            ),
            Triple(
                stringResource(R.string.przebiegniete_maratony),
                formatEquivalent(marathonsRun),
                stringResource(R.string.marathons_run_desc)
            ),
            Triple(
                stringResource(R.string.obejrzane_filmy),
                formatEquivalent(moviesWatched),
                stringResource(R.string.movies_watched_desc)
            ),
            Triple(
                stringResource(R.string.nowe_umiejetnosci),
                formatEquivalent(newSkills),
                stringResource(R.string.new_skills_desc)
            )
        )

        equivalents.forEach { (title, value, desc) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Share Button
        val context = LocalContext.current
        val roastText = com.example.scrolldebt.domain.usecases.BrutalTruthEngine(context).getWeeklyRoast(weekHours, language)

        val shareLabel = when (language.lowercase()) {
            "en" -> "SHARE MY SHAME"
            "es" -> "COMPARTIR MI VERGÜENZA"
            "fr" -> "PARTAGER MA HONTE"
            "de" -> "MEINE SCHANDE TEILEN"
            else -> "UDOSTĘPNIJ MÓJ WSTYD"
        }
        
        val titleText = stringResource(R.string.life_lost_title)
        val wastedText = stringResource(R.string.total_wasted)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable(
                    onClickLabel = shareLabel
                ) {
                    ShareUtils.shareWeeklyRoast(
                        context = context,
                        weeklyTimeMs = weekTotalMs,
                        roastText = roastText,
                        titleText = titleText,
                        wastedText = wastedText,
                        language = language
                    )
                }
                .semantics { contentDescription = shareLabel }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shareLabel,
                color = MaterialTheme.colorScheme.background,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
