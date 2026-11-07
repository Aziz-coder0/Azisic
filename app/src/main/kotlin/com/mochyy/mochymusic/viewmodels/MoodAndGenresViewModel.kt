// MoodAndGenresViewModel.kt

package com.mochyy.mochymusic.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.YTItem
import com.music.innertube.pages.MoodAndGenres
import com.mochyy.mochymusic.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

private object MoodCoverCache {
    val covers = mutableStateMapOf<String, List<String>>()
    val requested = java.util.Collections.synchronizedSet(mutableSetOf<String>())
}

fun coverKey(browseId: String, params: String?): String = "$browseId|${params.orEmpty()}"

@HiltViewModel
class MoodAndGenresViewModel
@Inject
constructor() : ViewModel() {
    private val _moodAndGenres = MutableStateFlow<List<MoodAndGenres>?>(null)
    val moodAndGenres = _moodAndGenres.asStateFlow()

    private val _loadFailed = MutableStateFlow(false)
    val loadFailed = _loadFailed.asStateFlow()
    private var loadJob: Job? = null

    val covers = MoodCoverCache.covers

    private val gate = Semaphore(3)

    init {
        loadGenres()
    }

    fun loadGenres() {
        if (loadJob?.isActive == true) return
        _loadFailed.value = false
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            YouTube.moodAndGenres()
                .onSuccess { _moodAndGenres.value = it }
                .onFailure {
                    reportException(it)
                    _loadFailed.value = true
                }
        }
    }

    fun requestCovers(browseId: String, params: String?) {
        val key = coverKey(browseId, params)
        if (!MoodCoverCache.requested.add(key)) return
        viewModelScope.launch(Dispatchers.IO) {
            gate.withPermit {
                YouTube.browse(browseId, params)
                    .onSuccess { result ->
                        val urls = result.items
                            .asSequence()
                            .flatMap { it.items.asSequence() }
                            .mapNotNull(YTItem::thumbnail)
                            .distinct()
                            .take(2)
                            .toList()
                        if (urls.isNotEmpty()) {
                            MoodCoverCache.covers[key] = urls
                        }
                    }
                    .onFailure {

                        MoodCoverCache.requested.remove(key)
                    }
            }
        }
    }
}
