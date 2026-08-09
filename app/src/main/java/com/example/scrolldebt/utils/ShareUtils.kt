package com.example.scrolldebt.utils

import com.example.scrolldebt.R

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun shareWeeklyRoast(context: Context, weeklyTimeMs: Long, roastText: String, titleText: String, wastedText: String, language: String) {
        val bitmap = generateRoastBitmap(context, weeklyTimeMs, roastText, titleText, wastedText, language)
        val imageUri = try {
            saveBitmapToCacheAndGetUri(context, bitmap)
        } finally {
            // A 1080x1920 ARGB_8888 buffer is ~8 MB. It is encoded to PNG by this point, so
            // hand it back immediately instead of waiting for the collector.
            bitmap.recycle()
        }

        if (imageUri == null) {
            Toast.makeText(context, R.string.share_failed, Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_intent_text))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_chooser_title))
        try {
            context.startActivity(chooser)
        } catch (e: android.content.ActivityNotFoundException) {
            // No app on the device can receive an image/png send.
            Toast.makeText(context, R.string.share_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateRoastBitmap(context: android.content.Context, weeklyTimeMs: Long, roastText: String, titleText: String, wastedText: String, language: String): Bitmap {
        // Full 1080x1920, the native size of an Instagram/TikTok story. This used to render
        // at 0.5 scale to save memory, which shipped a 540x960 image that visibly softened
        // once the story viewer upscaled it. The buffer is recycled by the caller instead.
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.parseColor("#0A0A0A")) // PureBlack

        // Background Tech Grid
        val gridPaint = Paint().apply {
            color = Color.parseColor("#161616")
            strokeWidth = 2f
        }
        val gridSize = 108f
        for (i in 0..width step gridSize.toInt()) {
            canvas.drawLine(i.toFloat(), 0f, i.toFloat(), height.toFloat(), gridPaint)
        }
        for (i in 0..height step gridSize.toInt()) {
            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), gridPaint)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F4F4F5") // StarkWhite
            textSize = 60f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        // Draw Minimalist Hourglass Icon
        val cx = width / 2f
        val cy = 180f
        val size = 30f
        val hgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF3B30")
            style = Paint.Style.FILL
        }
        val path = android.graphics.Path()
        path.moveTo(cx - size, cy - size)
        path.lineTo(cx + size, cy - size)
        path.lineTo(cx, cy)
        path.close()
        path.moveTo(cx - size, cy + size)
        path.lineTo(cx + size, cy + size)
        path.lineTo(cx, cy)
        path.close()
        canvas.drawPath(path, hgPaint)

        // Title
        canvas.drawText("SCROLLDEBT", width / 2f, 280f, paint)

        paint.color = Color.parseColor("#FF3B30") // AccentRed
        paint.textSize = 35f
        canvas.drawText(titleText.uppercase(), width / 2f, 350f, paint)

        // Calculate Time
        val totalMinutes = weeklyTimeMs / 1000 / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        // Dynamic Badge
        val badgeText = when {
            hours < 7 -> "ROOKIE"
            hours < 15 -> "SCROLL JUNKIE"
            hours < 30 -> "DIGITAL ZOMBIE"
            hours < 50 -> "NO LIFE"
            else -> "BRAIN DEAD"
        }
        val badgeColor = when {
            hours < 7 -> Color.parseColor("#4CAF50") // Green
            hours < 15 -> Color.parseColor("#FF9800") // Orange
            hours < 30 -> Color.parseColor("#FF3B30") // Red
            hours < 50 -> Color.parseColor("#9C27B0") // Purple
            else -> Color.parseColor("#8B0000") // Deep Red
        }

        // Draw Badge Background (Stroke)
        paint.color = badgeColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        val badgeRect = android.graphics.RectF(width / 2f - 220f, 420f, width / 2f + 220f, 500f)
        canvas.drawRoundRect(badgeRect, 16f, 16f, paint)

        // Draw Badge Text
        paint.style = Paint.Style.FILL
        paint.textSize = 40f
        paint.letterSpacing = 0.1f
        canvas.drawText("RANK: $badgeText", width / 2f, 475f, paint)
        paint.letterSpacing = 0f // reset

        // Time Text
        val timeStr = TimeFormatUtils.formatSmartTime(context, totalMinutes)
        paint.color = Color.parseColor("#F4F4F5")
        paint.textSize = 160f
        canvas.drawText(timeStr, width / 2f, 850f, paint)

        paint.color = Color.parseColor("#A1A1AA") // LightGray
        paint.textSize = 35f
        canvas.drawText(wastedText.uppercase(), width / 2f, 950f, paint)

        // Roast
        paint.color = Color.parseColor("#FF3B30")
        paint.textSize = 55f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        
        drawMultilineText(canvas, "\"$roastText\"", width / 2f, 1200f, paint, width - 200)

        // Footer
        paint.color = Color.parseColor("#555555")
        paint.textSize = 35f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        // Where the app actually lives. It is not on Play, and the old footer said it was.
        canvas.drawText("apkmasondev.github.io/scrolldebt-site", width / 2f, height - 150f, paint)

        return bitmap
    }

    private fun drawMultilineText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, maxWidth: Int) {
        val words = text.split(" ")
        var currentLine = ""
        var currentY = y

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val textWidth = paint.measureText(testLine)
            if (textWidth > maxWidth && currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentLine = word
                currentY += paint.descent() - paint.ascent() + 20f
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine, x, currentY, paint)
        }
    }

    private fun saveBitmapToCacheAndGetUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()

            // Always the same filename, so repeated shares overwrite rather than accumulate;
            // the directory can never hold more than this one image.
            val file = File(cachePath, "roast_summary.png")

            // use{} rather than a bare close(): if compress() throws, the previous version
            // leaked the file descriptor.
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            // Must match the authority declared for FileProvider in AndroidManifest, which is
            // ${applicationId}.fileprovider. applicationId differs from the package name of
            // the R class here, so derive it from the context rather than hardcoding.
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            android.util.Log.e("ShareUtils", "Failed to write share image", e)
            null
        }
    }
}
