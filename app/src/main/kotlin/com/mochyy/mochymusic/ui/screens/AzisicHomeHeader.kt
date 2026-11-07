package com.mochyy.mochymusic.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mochyy.mochymusic.R
import com.mochyy.mochymusic.models.MediaMetadata

@Composable
internal fun AzisicHomeHeader(
    track: MediaMetadata?,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onOpenPlayer: () -> Unit,
    onSearch: () -> Unit,
    onLiked: () -> Unit,
    onHistory: () -> Unit,
    onLibrary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.azisic_home_rotation),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onSurface,
        )
        BoxWithConstraints(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainer)
        ) {
            val coverWidth = (maxWidth * 0.43f).coerceAtMost(160.dp)
            val playLabel = stringResource(
                when {
                    track == null -> R.string.search
                    isCurrent && isPlaying -> R.string.pause
                    else -> R.string.play
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.width(coverWidth)
                        .aspectRatio(1f)
                        .background(colors.secondaryContainer)
                        .clickable(
                            enabled = isCurrent || track == null,
                            role = Role.Button,
                            onClickLabel = stringResource(if (isCurrent) R.string.now_playing else R.string.search),
                            onClick = if (isCurrent) onOpenPlayer else onSearch,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painterResource(R.drawable.ic_azisic_signal),
                        null,
                        tint = colors.primary,
                        modifier = Modifier.size(52.dp),
                    )
                    if (track?.thumbnailUrl != null) {
                        AsyncImage(track.thumbnailUrl, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
                Column(
                    Modifier.weight(1f).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = track?.title ?: stringResource(R.string.home_start_listening),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (track != null) {
                        Text(
                            text = track.artists.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(
                        onClick = if (track == null) onSearch else onPlay,
                        modifier = Modifier.align(Alignment.End).size(48.dp),
                    ) {
                        Box(
                            Modifier.size(42.dp).background(colors.primary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painterResource(
                                    when {
                                        track == null -> R.drawable.search_filled
                                        isCurrent && isPlaying -> R.drawable.pause
                                        else -> R.drawable.play
                                    }
                                ),
                                playLabel,
                                tint = colors.onPrimary,
                                modifier = Modifier.size(21.dp),
                            )
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeShortcut(stringResource(R.string.liked_songs), R.drawable.favorite, onLiked, Modifier.weight(1f))
            HomeShortcut(stringResource(R.string.history), R.drawable.history, onHistory, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeShortcut(stringResource(R.string.your_library), R.drawable.library_music, onLibrary, Modifier.weight(1f))
            HomeShortcut(stringResource(R.string.search), R.drawable.search_filled, onSearch, Modifier.weight(1f))
        }
    }
}

@Composable
private fun HomeShortcut(label: String, @DrawableRes icon: Int, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier.clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainerLow)
            .clickable(role = Role.Button, onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(icon), null, tint = colors.primary, modifier = Modifier.size(18.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
