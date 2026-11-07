package com.mochyy.mochymusic.ui.player.styles

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mochyy.mochymusic.LocalPlayerConnection
import com.mochyy.mochymusic.R
import com.mochyy.mochymusic.constants.PlayerStyle
import com.mochyy.mochymusic.ui.theme.AzisicBackground
import com.mochyy.mochymusic.ui.theme.AzisicSurface
import com.mochyy.mochymusic.ui.theme.AzisicSurfaceRaised
import com.mochyy.mochymusic.ui.theme.DefaultThemeColor
import com.mochyy.mochymusic.ui.utils.AppMotion
import com.mochyy.mochymusic.ui.utils.stableStatusBars
import com.mochyy.mochymusic.utils.makeTimeString

private val DeckLine = Color(0xFF503A68)
private val DeckText = Color(0xFFF6EFFB)
private val DeckMuted = Color(0xFFCDBFD8)
private val AzisicTemplates = listOf(PlayerStyle.PULSE_DECK, PlayerStyle.NIGHT_SESSION)

@Composable
internal fun AzisicPlayerTemplate(
    style: PlayerStyle,
    actions: StyleActions,
    modifier: Modifier,
    preview: Boolean,
) {
    val track = rememberStyleTrack()
    val session = style == PlayerStyle.NIGHT_SESSION
    val tones = rememberStyleTones().copy(chip = Color.Transparent, ink = DeckText)
    val interactive = LocalStyleInteractive.current
    val statusTop = WindowInsets.stableStatusBars.asPaddingValues().calculateTopPadding()

    StyleFrame(
        preview = preview,
        modifier = modifier,
        tones = tones,
        background = {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(if (session) Color(0xFF2C1C39) else Color(0xFF433052), AzisicBackground)
                    )
                )
            )
        },
    ) { landscape, _, _ ->
        BoxWithConstraints(
            Modifier.fillMaxSize().navigationBarsPadding().padding(
                start = 20.dp,
                end = 20.dp,
                top = if (landscape) 8.dp else statusTop + 12.dp,
                bottom = 14.dp,
            )
        ) {
            val compact = maxHeight < 525.dp
            val sessionCoverSize = minOf(maxWidth * 0.43f, if (compact) 102.dp else 154.dp)
            val canInteract = !preview && interactive()
            val header: @Composable () -> Unit = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = actions.onClose, enabled = canInteract) {
                        Icon(painterResource(R.drawable.expand_more), stringResource(R.string.close), tint = DeckText)
                    }
                    Text(
                        stringResource(R.string.now_playing),
                        style = MaterialTheme.typography.titleSmall,
                        color = DeckText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    IconButton(onClick = actions.onOtherMenu, enabled = canInteract) {
                        Icon(painterResource(R.drawable.more_horiz), stringResource(R.string.more_options), tint = DeckText)
                    }
                }
            }
            val controls: @Composable () -> Unit = {
                DeckControls(style, actions, tones, canInteract)
            }

            if (landscape) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Column(Modifier.weight(0.9f).fillMaxSize()) {
                        header()
                        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            DeckArtwork(track.artwork, minOf(maxWidth, maxHeight), session, preview)
                        }
                        DeckTrackTitle(track, compact = true, interactive = canInteract)
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { controls() }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 9.dp),
                ) {
                    header()
                    if (session) {
                        Row(
                            Modifier.weight(1f).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            DeckArtwork(track.artwork, sessionCoverSize, true, preview)
                            Column(Modifier.weight(1f)) {
                                DeckTrackTitle(track, compact = true, interactive = canInteract)
                            }
                        }
                    } else {
                        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            DeckArtwork(track.artwork, minOf(maxWidth, maxHeight), false, preview)
                        }
                        DeckTrackTitle(track, compact, canInteract)
                    }
                    controls()
                }
            }
        }
    }
}

@Composable
private fun DeckTrackTitle(track: StyleTrack, compact: Boolean, interactive: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = track.title.ifBlank { stringResource(R.string.azisic_choose_track) },
                color = DeckText,
                style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                maxLines = if (compact) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (track.artist.isNotBlank()) {
                Text(
                    track.artist, color = DeckMuted, style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
        val connection = LocalPlayerConnection.current
        if (connection != null) {
            val current by connection.uiState.trackInfo.collectAsState()
            IconButton(onClick = connection::toggleLike, enabled = interactive) {
                Icon(
                    painterResource(if (current.liked) R.drawable.favorite else R.drawable.favorite_border),
                    stringResource(if (current.liked) R.string.azisic_unlike_track else R.string.like),
                    tint = if (current.liked) DefaultThemeColor else DeckText,
                )
            }
        }
    }
}

@Composable
private fun DeckArtwork(url: String?, size: Dp, session: Boolean, preview: Boolean) {
    val clock = rememberLivePosition(animated = session && !preview && AppMotion.durationScale.scaleFactor > 0)
    val shape = if (session) CircleShape else RoundedCornerShape(8.dp)
    Box(
        Modifier.size(size.coerceAtLeast(0.dp)).clip(shape)
            .background(if (session) AzisicBackground else AzisicSurfaceRaised),
        contentAlignment = Alignment.Center,
    ) {
        if (session) {
            Canvas(Modifier.fillMaxSize()) {
                for (index in 1..8) {
                    drawCircle(DeckLine, radius = this.size.minDimension * (0.19f + 0.033f * index), style = Stroke(1.dp.toPx()))
                }
            }
        }
        AnimatedContent(
            targetState = url,
            transitionSpec = { fadeIn(tween(330)) togetherWith fadeOut(tween(180)) },
            modifier = Modifier.fillMaxSize(if (session) 0.56f else 1f).clip(shape).graphicsLayer {
                if (session && !preview && AppMotion.durationScale.scaleFactor > 0) {
                    rotationZ = (clock.positionMs() % 15_000L) * (360f / 15_000f)
                }
            },
            label = "Azisic album artwork",
        ) { artwork ->
            if (artwork == null) {
                Box(Modifier.fillMaxSize().background(AzisicSurfaceRaised), contentAlignment = Alignment.Center) {
                    Icon(painterResource(R.drawable.ic_mochy_mark), null, tint = DefaultThemeColor, modifier = Modifier.size(38.dp))
                }
            } else {
                AsyncImage(artwork, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
    }
}

@Composable
private fun DeckControls(style: PlayerStyle, actions: StyleActions, tones: StyleTones, interactive: Boolean) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DeckSeekBar(interactive)
        TransportCircles(
            tones = tones,
            size = 48.dp,
            spacing = 18.dp,
            playTones = tones.copy(chip = DefaultThemeColor, ink = AzisicBackground),
            playSize = 60.dp,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { actions.onLyrics?.invoke() }, enabled = interactive && actions.onLyrics != null) {
                Text(stringResource(R.string.lyrics))
            }
            TextButton(onClick = { actions.onQueue?.invoke() }, enabled = interactive && actions.onQueue != null) {
                Text(stringResource(R.string.queue))
            }
            TextButton(onClick = actions.onEditPlayer, enabled = interactive) {
                Text(stringResource(R.string.azisic_player_layouts))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (template in AzisicTemplates) {
                val selected = template == style
                val fill by animateColorAsState(if (selected) AzisicSurfaceRaised else AzisicSurface, label = "Template selection")
                TextButton(
                    onClick = { actions.onSelectTemplate?.invoke(template) },
                    enabled = interactive && actions.onSelectTemplate != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(containerColor = fill, contentColor = if (selected) DefaultThemeColor else DeckMuted),
                ) {
                    Text(template.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DeckSeekBar(interactive: Boolean) {
    val connection = LocalPlayerConnection.current ?: return
    val progress by connection.uiState.progressState.collectAsState()
    val duration = progress.duration.coerceAtLeast(0L)
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val played = if (duration > 0L) (progress.position.toFloat() / duration).coerceIn(0f, 1f) else 0f
    Slider(
        value = dragFraction ?: played,
        onValueChange = { dragFraction = it },
        onValueChangeFinished = {
            dragFraction?.let { connection.seekTo((it * duration).toLong()) }
            dragFraction = null
        },
        enabled = interactive && duration > 0L,
        modifier = Modifier.fillMaxWidth().height(36.dp),
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(makeTimeString(dragFraction?.let { (it * duration).toLong() } ?: progress.position.coerceAtLeast(0L)), color = DeckMuted, fontSize = 11.sp)
        Text(makeTimeString(duration), color = DeckMuted, fontSize = 11.sp)
    }
}
