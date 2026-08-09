package com.example.scrolldebt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.example.scrolldebt.ui.theme.StarkWhite

@Composable
fun DoomscrollCard(
    modifier: Modifier = Modifier,
    borderColor: Color = StarkWhite.copy(alpha = 0.1f),
    backgroundColor: Color = Color.Transparent,
    cornerRadius: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var finalModifier = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)
        .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))

    if (onClick != null) {
        finalModifier = finalModifier.clickable { onClick() }
    }

    finalModifier = finalModifier.padding(contentPadding)

    Box(
        modifier = finalModifier,
        content = content
    )
}
