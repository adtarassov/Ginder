package com.adtarassov.ginder.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adtarassov.ginder.domain.GetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val getUseCase: GetUseCase,
) : ViewModel() {

  init {
    viewModelScope.launch {
      val res = getUseCase.searchRepos("test")
      Log.d("MainViewModel", res.toString())
    }
  }

}