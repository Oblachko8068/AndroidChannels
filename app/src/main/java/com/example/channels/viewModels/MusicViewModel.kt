package com.example.channels.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Music
import com.example.domain.repository.MusicDownloadRepository
import com.example.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MusicViewModel @Inject constructor(
    musicRepository: MusicRepository,
    musicDownloadRepository: MusicDownloadRepository,
    coroutineContext: CoroutineContext
): ViewModel() {

    private var musicLiveData:LiveData<List<Music>> = musicRepository.getMusicList()

    init {
        viewModelScope.launch(coroutineContext) {
            musicDownloadRepository.fetchMusic()
        }
    }

    fun getMusicLiveData() : LiveData<List<Music>> = musicLiveData

    fun getMusicList(): List<Music> = musicLiveData.value ?: emptyList()

    fun getFilteredMusic(): List<Music> {
        val music = getMusicList()
        val searchQuery = _searchTextLiveData.value
        return if (!searchQuery.isNullOrEmpty()){
            music.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        } else {
            music
        }
    }

    companion object {
        private val _searchTextLiveData = MutableLiveData<String>()
        val searchTextLiveData: LiveData<String> = _searchTextLiveData
        fun setSearchText(searchText: String) {
            _searchTextLiveData.value = searchText
        }
    }
}