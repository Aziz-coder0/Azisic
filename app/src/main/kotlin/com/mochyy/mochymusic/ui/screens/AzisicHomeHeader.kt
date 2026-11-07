package com.mochyy.mochymusic.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onOpenPlayer: () -> Unit,
    onSearch: () -> Unit,
    onLiked: () -> Unit,
    onHistory: () -> Unit,
    onLibrary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeShortcut(stringResource(R.string.liked_songs), R.drawable.favorite, colors.primaryContainer, onLiked, Modifier.weight(1f))
            HomeShortcut(stringResource(R.string.history), R.drawable.history, colors.secondaryContainer, onHistory, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeShortcut(stringResource(R.string.your_library), R.drawable.library_music, colors.surfaceContainerHighest, onLibrary, Modifier.weight(1f))
            HomeShortcut(stringResource(R.string.search), R.drawable.search_filled, colors.surfaceContainerHigh, onSearch, Modifier.weight(1f))
        }
        if (track != null) {
            Text(
                text = stringResource(R.string.now_playing),
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
            )
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceContainer)
                    .clickable(role = Role.Button, onClick = onOpenPlayer)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)).background(colors.surfaceContainerHighest),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.music_note), null, tint = colors.onSurfaceVariant)
                    AsyncImage(track.thumbnailUrl, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Column(Modifier.weight(1f)) {
                    Text(track.title, style = MaterialTheme.typography.titleMedium, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        track.artists.joinToString(", ") { it.name },
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(Modifier.size(36.dp).background(colors.primary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(
                        painterResource(R.drawable.arrow_forward), null,
                        tint = colors.onPrimary, modifier = Modifier.size(19.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShortcut(label: String, @DrawableRes icon: Int, tileColor: Color, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier.clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainer)
            .clickable(role = Role.Button, onClick = onClick)
            .heightIn(min = 56.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(48.dp, 56.dp).background(tileColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), null, tint = colors.onSurface, modifier = Modifier.size(21.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = colors.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 7.dp),
        )
    }
}
