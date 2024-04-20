package com.example.channels.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Radio
import com.example.domain.repository.RadioDownloadRepository
import com.example.domain.repository.RadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class RadioViewModel @Inject constructor(
    radioDownloadRepository: RadioDownloadRepository,
    radioRepository: RadioRepository,
    coroutineContext: CoroutineContext
) : ViewModel() {

    private var radioLiveData: LiveData<List<Radio>> =
        radioRepository.getRadioListLiveData()

    init {
        viewModelScope.launch(coroutineContext) {
            radioDownloadRepository.fetchRadio()
        }
    }

    fun getRadioLiveData(): LiveData<List<Radio>> = radioLiveData
}