// AppNavigation.kt

package com.mochyy.mochymusic.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mochyy.mochymusic.LocalGlassState
import com.mochyy.mochymusic.constants.ChromeGlassStyle
import com.mochyy.mochymusic.constants.ChromeGlassStyleKey
import com.mochyy.mochymusic.constants.SlimNavBarHeight
import com.mochyy.mochymusic.ui.screens.Screens
import com.mochyy.mochymusic.utils.rememberEnumPreference
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size

@Immutable
private data class NavItemState(
    val isSelected: Boolean,
    val iconRes: Int
)

/**
 * Navigates to a top-level tab exactly the way the navigation bar does.
 *
 * Anything that jumps to a tab from inside another tab MUST use this rather than a plain
 * `navigate(route)`, and the reason is subtle enough to be worth spelling out.
 *
 * A plain `navigate("settings")` from the profile menu pushes Settings on top of the *Home* tab's
 * stack, because that is where you were standing. Tapping Home in the bar then runs
 * `popUpTo(startDestination) { saveState = true }`, which sweeps that pushed Settings entry into
 * the state saved *for Home* — and `restoreState = true` on the very same call immediately puts it
 * back. The result is that tapping Home lands you on Settings, which is precisely the bug this
 * fixes, and it looks like the bar is ignoring you when in fact it is faithfully restoring a stack
 * that Settings should never have been part of.
 *
 * Going through here instead means a tab is always entered *as* a tab, so it never becomes a child
 * of whichever tab happened to be on screen when you left.
 */
fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * a tap on a tab. another tab: switch to it, back on the page you left it on. the tab you're in:
 * from one of its pages, back to the tab's own page (as every tab bar does); already there, the
 * page scrolls to its top.
 */
fun NavController.onTabTapped(screen: Screens, isSelectedTab: Boolean, scrollToTop: () -> Unit) {
    if (!isSelectedTab) {
        navigateToTab(screen.route)
        return
    }
    if (currentDestination?.route == screen.route) {
        currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
        scrollToTop()
        return
    }
    if (!popBackStack(screen.route, inclusive = false)) navigateToTab(screen.route)
}

@Stable
private fun isRouteSelected(currentRoute: String?, screenRoute: String, navigationItems: List<Screens>): Boolean {
    if (currentRoute == null) return false
    if (currentRoute == screenRoute) return true
    return navigationItems.any { it.route == screenRoute } &&
           currentRoute.startsWith("$screenRoute/")
}

private val RouteOwners: List<Pair<String, String>> = listOf(
    "search/" to "search_input",
    "genre/" to "search_input",
    "youtube_browse/" to "search_input",
    "browse/" to "search_input",
    "library/" to "library",
    "cipher_settings" to "settings",
    "playback_diagnostics" to "settings",
    "mochy_settings" to "settings",
    "general_settings" to "settings",
    "experimental_settings" to "settings",
    "developer_playground" to "settings",
    "storage_settings" to "settings",
    "import_sync" to "settings",
    "import_providers" to "settings",
    "import_tunemymusic" to "settings",
    "import_progress" to "settings",
    "youtube_sync" to "settings",
    "youtube_login" to "settings",
    "account_import/" to "settings",
    "equalizer" to "settings",
)

private fun owningTabRoute(currentRoute: String?, navigationItems: List<Screens>): String? {
    if (currentRoute == null) return null
    navigationItems.firstOrNull { isRouteSelected(currentRoute, it.route, navigationItems) }
        ?.let { return it.route }
    RouteOwners.firstOrNull { (prefix, _) ->
        currentRoute == prefix || currentRoute.startsWith(prefix)
    }?.let { return it.second }

    // Catch-all so RouteOwners does not have to stay exhaustive: every settings sub-screen is
    // owned by the settings tab without anyone having to list it. Only a fallback now; the back
    // stack decides the tab whenever it has a tab page in it (see owningTab).
    if (currentRoute.endsWith("_settings") || currentRoute.startsWith("settings")) return "settings"
    return null
}

/**
 * the tab the page on screen belongs to: the nearest tab page under it in the back stack.
 *
 * the bar used to remember the last tab it could name from the current route alone, and keep it
 * for routes no tab claims (albums, playlists, artists). that went wrong two ways: switching back
 * to a tab restores the page you left it on, so coming back to the Library's album kept Home lit;
 * and the remembered value was dropped whenever the bar left the screen (the player's full view,
 * the recap), so it came back as Home. the back stack always knows: a tab is entered with
 * [navigateToTab], so every page sits above the tab page it was opened from.
 */
fun owningTab(backStack: List<NavBackStackEntry>, navigationItems: List<Screens>): String {
    val tabRoutes = navigationItems.mapTo(HashSet()) { it.route }
    backStack.asReversed().firstOrNull { it.destination.route in tabRoutes }
        ?.destination?.route
        ?.let { return it }
    // a stack without a tab page under it (a deep link): go by the route, else the first tab
    return owningTabRoute(backStack.lastOrNull()?.destination?.route, navigationItems)
        ?: navigationItems.firstOrNull()?.route.orEmpty()
}

/**
 * which tab each page belongs to, kept by entry so a page that has already left the stack (the
 * one a tab switch saved away) can still be placed. the tab slide reads this: an album open in
 * the Library slides like the Library, not like a page no tab owns.
 */
class TabOwnership(private val navigationItems: List<Screens>) {
    private val tabRoutes = navigationItems.map { it.route }
    private val owners = HashMap<String, String>()

    fun update(backStack: List<NavBackStackEntry>) {
        var owner: String? = null
        backStack.forEach { entry ->
            val route = entry.destination.route
            if (route != null && route in tabRoutes) owner = route
            owner?.let { owners[entry.id] = it }
        }
        // saved tab stacks keep their entries' ids, so only trim when it has clearly grown stale
        if (owners.size > 400) {
            val live = backStack.mapTo(HashSet()) { it.id }
            owners.keys.retainAll(live)
        }
    }

    /** the tab's position in the bar, or -1 when nothing places it */
    fun indexOf(entry: NavBackStackEntry, backStack: List<NavBackStackEntry>): Int {
        update(backStack)
        val owner = owners[entry.id] ?: entry.destination.route?.takeIf { it in tabRoutes }
            ?: owningTabRoute(entry.destination.route, navigationItems)
        return tabRoutes.indexOf(owner)
    }
}

/** The bar itself, a stadium the mini player's 64dp matches. */
internal val NavBarHeight = 64.dp
internal val NavBarShape = RoundedCornerShape(50)

/** The lit tab's highlight sits this far in from the bar's top, bottom and both ends. */
private val NavHighlightInset = 8.dp

/** Room between a highlight and where its neighbour's would be. */
private val NavHighlightGap = 4.dp

// half the gap is the row's padding and half is inside each slot, so the highlight sits as far from the ends as from the top and bottom
internal val NavRowPadding = NavHighlightInset - NavHighlightGap / 2

/** How bright an unlit tab's icon is. */
private const val UnlitIconAlpha = 0.7f

/**
 * The highlight's front edge springs to the new tab faster than its back edge, so it stretches
 * toward the tab and settles into it instead of sliding over as a rigid block.
 */
private val NavHighlightLead = spring<Float>(dampingRatio = 0.82f, stiffness = 700f)
private val NavHighlightTrail = spring<Float>(dampingRatio = 0.82f, stiffness = 320f)

@Composable
fun AppNavigationBar(
    navigationItems: List<Screens>,
    /** from [owningTab]: the tab the page on screen belongs to */
    selectedTabRoute: String,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    /** a second quick tap on Search: straight into typing */
    onSearchDoubleTap: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val glassStyle by rememberEnumPreference(ChromeGlassStyleKey, defaultValue = ChromeGlassStyle.FROSTED)
    Box(modifier.fillMaxWidth().background(if (pureBlack) Color.Black else colors.background)) {
        if (glassStyle == ChromeGlassStyle.MODULE) {
            FrostedChromeGlass(
                state = LocalGlassState.current ?: remember { GlassState() },
                pureBlack = pureBlack,
                accentWash = rememberChromeAccentWash(),
                modifier = Modifier.matchParentSize(),
            )
        }
        Row(
            modifier = Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(if (slimNav) SlimNavBarHeight else NavBarHeight + 16.dp)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationItems.forEach { screen ->
                NavTab(
                    screen = screen,
                    isSelected = screen.route == selectedTabRoute,
                    onItemClick = onItemClick,
                    onDoubleTap = onSearchDoubleTap.takeIf { screen == Screens.Search },
                    showLabel = !slimNav,
                )
            }
        }
    }
}

/**
 * One highlight behind the lit tab that slides to the next one, instead of each tab fading its own
 * in and out. Only read while drawing, so the slide never recomposes the bar.
 */
@Composable
internal fun Modifier.navHighlight(selectedIndex: Int, tabCount: Int): Modifier {
    val start = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val end = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val shown = remember { Animatable(if (selectedIndex >= 0) 1f else 0f) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) {
            shown.animateTo(0f, tween(200))
            return@LaunchedEffect
        }
        val target = selectedIndex.toFloat()
        // back from no lit tab, it fades in where it belongs rather than sliding over from the old one
        if (shown.value == 0f) {
            start.snapTo(target)
            end.snapTo(target)
        }
        launch { shown.animateTo(1f, tween(200)) }
        val movingRight = target > (start.value + end.value) / 2f
        launch { start.animateTo(target, if (movingRight) NavHighlightTrail else NavHighlightLead) }
        end.animateTo(target, if (movingRight) NavHighlightLead else NavHighlightTrail)
    }

    return drawBehind {
        val alpha = shown.value
        if (alpha <= 0f || tabCount == 0) return@drawBehind
        val slot = size.width / tabCount
        val halfGap = NavHighlightGap.toPx() / 2f
        val inset = NavHighlightInset.toPx()
        val left = start.value * slot + halfGap
        val right = (end.value + 1f) * slot - halfGap
        val height = size.height - inset * 2f
        drawRoundRect(
            color = ChromeHighlightColor,
            topLeft = Offset(left, inset),
            size = Size(right - left, height),
            cornerRadius = CornerRadius(height / 2f),
            alpha = alpha,
        )
    }
}

/**
 * A tab's tap, or with [onDoubleTap] a second tap close behind the first: the first still goes to
 * the tab at once, the second does [onDoubleTap] instead. [lastTap] holds the time of the one before.
 */
private fun tabTapped(lastTap: LongArray, doubleTapTimeoutMillis: Long, onDoubleTap: (() -> Unit)?, onTap: () -> Unit) {
    val now = android.os.SystemClock.uptimeMillis()
    if (onDoubleTap != null && now - lastTap[0] < doubleTapTimeoutMillis) {
        lastTap[0] = 0L
        onDoubleTap()
    } else {
        lastTap[0] = now
        onTap()
    }
}

/**
 * One tab of the bar. Its icon swaps to the filled one and brightens as it lights up, and it
 * squishes under a press. [onDoubleTap] is for Search, where two taps open the search field.
 * The Library's icon turns into a download badge while songs download.
 */
@Composable
internal fun RowScope.NavTab(
    screen: Screens,
    isSelected: Boolean,
    onItemClick: (Screens, Boolean) -> Unit,
    onDoubleTap: (() -> Unit)?,
    showLabel: Boolean = false,
) {
    val viewConfiguration = LocalViewConfiguration.current
    val interactionSource = remember { MutableInteractionSource() }
    val lastTap = remember { longArrayOf(0L) }
    val lit = animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200),
        label = "nav_tab_lit"
    )

    // Both icons stay loaded, so lighting a tab never shows an empty frame while its filled icon loads.
    val unlitIcon = coil3.compose.rememberAsyncImagePainter(model = screen.iconIdInactive)
    val litIcon = coil3.compose.rememberAsyncImagePainter(model = screen.iconIdActive)
    val iconSize = if (screen == Screens.Home || screen == Screens.Search) 22.dp else 24.dp

    val colors = MaterialTheme.colorScheme
    val label = stringResource(screen.titleId)
    val tint = if (isSelected) colors.primary else colors.onSurfaceVariant

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .semantics { selected = isSelected }
            .pressBounce(interactionSource, pressedScale = 0.85f)
            .clickable(
                interactionSource = interactionSource,
                indication = RemoteFocus,
                role = Role.Tab,
                onClick = {
                    tabTapped(lastTap, viewConfiguration.doubleTapTimeoutMillis, onDoubleTap) {
                        onItemClick(screen, isSelected)
                    }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        val icon: @Composable () -> Unit = {
            TabIcons(label.takeUnless { showLabel }, unlitIcon, litIcon, iconSize, tint) { lit.value }
        }
        if (screen == Screens.Library) LibraryDownloadIcon(icon) else icon()
        if (showLabel) {
            Text(
                label,
                color = tint,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }
}

/** a tab's two icons, the filled one fading in over the outline as the tab lights up */
@Composable
private fun TabIcons(
    label: String?,
    unlitIcon: androidx.compose.ui.graphics.painter.Painter,
    litIcon: androidx.compose.ui.graphics.painter.Painter,
    iconSize: androidx.compose.ui.unit.Dp,
    tint: Color,
    lit: () -> Float,
) {
    Box(contentAlignment = Alignment.Center) {
        Icon(
            painter = unlitIcon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    alpha = UnlitIconAlpha * (1f - lit())
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
        )
        Icon(
            painter = litIcon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    alpha = lit()
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
        )
    }
}
