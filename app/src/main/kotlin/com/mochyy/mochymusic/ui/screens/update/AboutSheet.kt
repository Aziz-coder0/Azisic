package com.mochyy.mochymusic.ui.screens.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mochyy.mochymusic.BuildConfig
import com.mochyy.mochymusic.R
import com.mochyy.mochymusic.core.updater.GithubRepoUrl
import com.mochyy.mochymusic.ui.component.PopupSheetState

private data class LicenseEntry(
    val tag: String,
    val title: String,
    val subtitle: String,
    val url: String,
)

private val Licenses = listOf(
    LicenseEntry(
        tag = "GPL-3.0",
        title = "Mochy Music · GNU General Public License v3.0",
        subtitle = "Free and open source software, source code on GitHub",
        url = "$GithubRepoUrl/blob/main/LICENSE",
    ),
    LicenseEntry(
        tag = "GPL-3.0",
        title = "ViviMusic · GNU General Public License v3.0",
        subtitle = "by vivizzz007 · base project this app was forked from",
        url = "https://github.com/vivizzz007/vivi-music/blob/main/LICENSE",
    ),
    LicenseEntry(
        tag = "GPL-3.0",
        title = "Metrolist · GNU General Public License v3.0",
        subtitle = "by Mostafa Alagamy · base framework",
        url = "https://github.com/mostafaalagamy/Metrolist/blob/main/LICENSE",
    ),
    LicenseEntry(
        tag = "GPL-3.0",
        title = "SimpMusic · GNU General Public License v3.0",
        subtitle = "by maxrave-dev · lyrics provider integration",
        url = "https://github.com/maxrave-dev/SimpMusic/blob/dev/LICENSE",
    ),
    LicenseEntry(
        tag = "GPL-3.0",
        title = "ArchiveTune · GNU General Public License v3.0",
        subtitle = "by Rukamori · design and architectural inspiration",
        url = "https://github.com/rukamori/ArchiveTune/blob/main/LICENSE",
    ),
    LicenseEntry(
        tag = "GPL-3.0",
        title = "RiMusic · GNU General Public License v3.0",
        subtitle = "by fast4x · InnerTube API wrapper",
        url = "https://github.com/fast4x/RiMusic/blob/master/LICENSE",
    ),
    LicenseEntry(
        tag = "MIT",
        title = "LRCLIB · MIT License",
        subtitle = "by tranxuanthang · synchronized lyrics database",
        url = "https://github.com/tranxuanthang/lrclib/blob/main/LICENSE",
    ),
    LicenseEntry(
        tag = "MIT",
        title = "AutoEq · MIT License",
        subtitle = "by Jaakko Pasanen · headphone correction profiles",
        url = "https://github.com/jaakkopasanen/AutoEq/blob/master/LICENSE",
    ),
)

//  this guy promote the app
fun PopupSheetState.showAboutSheet() {
    val popup = this
    show(buttonBar = { SheetPrimaryButton(text = "Done", onClick = popup::dismiss) }) {
        AboutSheetContent { popup.showAzisicLicenses() }
    }
}

@Composable
private fun AboutSheetContent(onLicenses: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(painterResource(R.drawable.ic_mochy_mark), null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
                Text("Version ${BuildConfig.VERSION_NAME}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(stringResource(R.string.azisic_developer_title), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Text(stringResource(R.string.azisic_developer_name), style = MaterialTheme.typography.titleLarge)
        }
        TextButton(onClick = onLicenses) { Text("Open-source licenses") }
    }
}

private fun PopupSheetState.showAzisicLicenses() {
    val popup = this
    show(buttonBar = { SheetPrimaryButton(text = "Done", onClick = popup::dismiss) }) {
        val context = LocalContext.current
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 12.dp)) {
            Text("Open-source licenses", style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.azisic_upstream_credit), modifier = Modifier.padding(vertical = 8.dp))
            Licenses.forEach { entry ->
                TextButton(onClick = { context.openUrl(entry.url) }) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(entry.title)
                        Text("${entry.tag} · ${entry.subtitle}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
