// LibraryTabs.kt
//
// The All / Local / Cached / Imported switch at the top of the Library, and the small pieces the
// tabs share: the two-wide card grid, the caps section label and the "Manage" pill of a tab.

package com.mochyy.mochymusic.ui.screens.library

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mochyy.mochymusic.R
import com.mochyy.mochymusic.ui.screens.search.SearchColors
import com.mochyy.mochymusic.ui.screens.search.SearchHorizontalPadding
import com.mochyy.mochymusic.ui.theme.AzisicBackground
import com.mochyy.mochymusic.ui.theme.AzisicSurfaceRaised
import com.mochyy.mochymusic.ui.theme.DefaultThemeColor
import com.mochyy.mochymusic.viewmodels.LibraryShelfKind
import com.mochyy.mochymusic.ui.component.RemoteFocus

/**
 * What the Library shows: everything, what plays from the phone, what the player has cached, or
 * what was imported. [shelf] is the slice of the library a tab is built from, if it is one.
 */
enum class LibraryTab(val labelRes: Int, val shelf: LibraryShelfKind? = null) {
    All(R.string.library_tab_all),
    Local(R.string.library_tab_local, LibraryShelfKind.Local),
    Cached(R.string.cached, LibraryShelfKind.Cached),
    Imported(R.string.library_tab_imported),
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryTabChips(
    selected: LibraryTab,
    onSelect: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LibraryTab.entries.forEach { tab ->
            LibraryTabChip(tab, isSelected = tab == selected, onSelect = onSelect)
        }
    }
}

@Composable
private fun LibraryTabChip(tab: LibraryTab, isSelected: Boolean, onSelect: (LibraryTab) -> Unit) {
    val background by animateColorAsState(
        targetValue = if (isSelected) DefaultThemeColor else AzisicSurfaceRaised,
        animationSpec = tween(220),
        label = "libraryTabChip",
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .semantics { selected = isSelected }
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = RemoteFocus,
                onClick = { onSelect(tab) },
            )
            .heightIn(min = 38.dp)
            .padding(horizontal = 14.dp, vertical = 5.dp),
    ) {
        Text(
            text = stringResource(tab.labelRes),
            color = if (isSelected) AzisicBackground else SearchColors.Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** "ALL MUSIC", "PLAYLISTS": a small caps label over a group of cards. */
@Composable
fun LibraryCapsLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = SearchColors.Primary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = SearchHorizontalPadding),
    )
}

/** The small centred pill at the bottom of a tab that opens its Manage sheet. */
@Composable
fun LibraryManagePill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(AzisicSurfaceRaised)
                .clickable(role = Role.Button, onClick = onClick)
                .heightIn(min = 30.dp)
                .padding(horizontal = 18.dp, vertical = 6.dp),
        ) {
            Text(
                text = text,
                color = SearchColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** One card in a [LibraryCardGrid]. */
@Immutable
class LibraryCardEntry(
    val title: String,
    val count: Int,
    val covers: List<String?>,
    val onClick: () -> Unit,
    val roundCovers: Boolean = false,
    val icon: (@Composable () -> Unit)? = null,
)

/** Cards two to a row, the last one keeping its half width when it is alone. */
@Composable
fun LibraryCardGrid(entries: List<LibraryCardEntry>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = SearchHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        entries.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { entry ->
                    LibraryCategoryCard(
                        title = entry.title,
                        count = entry.count,
                        covers = entry.covers,
                        onClick = entry.onClick,
                        icon = entry.icon,
                        roundCovers = entry.roundCovers,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
