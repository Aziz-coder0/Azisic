package com.mochyy.mochymusic.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mochyy.mochymusic.ui.theme.AzisicBackground
import com.mochyy.mochymusic.ui.theme.AzisicSurface
import com.mochyy.mochymusic.ui.theme.DefaultThemeColor

@Composable
internal fun AzisicHomeHeader(
    trackTitle: String?,
    onPlayCurrent: () -> Unit,
    onSearch: () -> Unit,
    onLiked: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panel = RoundedCornerShape(28.dp)
    var drawn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { drawn = true }
    val recordReveal by animateFloatAsState(if (drawn) 1f else 0.5f, tween(540), label = "Home record reveal")

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.fillMaxWidth().clip(panel)
                .background(Brush.linearGradient(listOf(Color(0xFF45265E), Color(0xFF25183D))))
                .border(1.dp, Color(0xFF674881), panel)
        ) {
            Canvas(
                Modifier.size(190.dp).align(Alignment.CenterEnd).graphicsLayer {
                    alpha = recordReveal * 0.75f
                    scaleX = recordReveal
                    scaleY = recordReveal
                }
            ) {
                val center = Offset(size.width * 0.86f, size.height * 0.54f)
                for (groove in 1..9) {
                    drawCircle(
                        color = DefaultThemeColor.copy(alpha = if (groove == 9) 0.40f else 0.13f),
                        radius = size.minDimension * (0.07f + groove * 0.055f),
                        center = center,
                        style = Stroke(1.dp.toPx()),
                    )
                }
                drawCircle(DefaultThemeColor, radius = 4.dp.toPx(), center = center)
            }
            Column(
                Modifier.fillMaxWidth(0.82f).padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("LISTENING ROOM", color = DefaultThemeColor, fontSize = 10.sp, letterSpacing = 2.2.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (trackTitle == null) "Find your next\nfavorite." else trackTitle,
                    color = Color(0xFFF8EFFC),
                    fontSize = 30.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                FilledTonalButton(
                    onClick = if (trackTitle == null) onSearch else onPlayCurrent,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = DefaultThemeColor, contentColor = AzisicBackground),
                ) {
                    Text(if (trackTitle == null) "Search music" else "Open player", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeShortcut("Liked songs", onLiked, Modifier.weight(1f))
            HomeShortcut("History", onHistory, Modifier.weight(1f))
        }
    }
}

@Composable
private fun HomeShortcut(label: String, onClick: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier.clip(shape).background(AzisicSurface).border(1.dp, Color(0xFF3B2A50), shape)
            .clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color(0xFFF8EFFC), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("↗", color = DefaultThemeColor, fontSize = 13.sp)
    }
}
