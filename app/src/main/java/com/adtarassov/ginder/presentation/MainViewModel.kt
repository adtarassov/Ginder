package com.adtarassov.ginder.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adtarassov.ginder.data.RepositoryResponseModel
import com.adtarassov.ginder.data.ResponseState.Error
import com.adtarassov.ginder.data.ResponseState.Success
import com.adtarassov.ginder.domain.SearchRepositoryUseCase
import com.adtarassov.ginder.presentation.MainEvent.OnSearchClick
import com.adtarassov.ginder.presentation.MainEvent.OnSearchTextChange
import com.adtarassov.ginder.presentation.MainEvent.OnSwipeLeft
import com.adtarassov.ginder.presentation.MainEvent.OnSwipeRight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val searchRepositoryUseCase: SearchRepositoryUseCase,
) : ViewModel() {

  private val _viewStateFlow = MutableStateFlow(MainState.EMPTY)
  val viewStateFlow = _viewStateFlow.asStateFlow()

  private var loadingJob: Job? = null

  private var currentIndex = 0

  private val items = mutableListOf<CardUiModel>()

  fun obtainEvent(event: MainEvent) {
    when (event) {

      OnSwipeRight -> {
        val cardTop = viewStateFlow.value.cardTop
        Log.d("MainViewModel", "You liked ${cardTop?.text}")
        swipe()
      }

      OnSwipeLeft -> {
        val cardTop = viewStateFlow.value.cardTop
        Log.d("MainViewModel", "You disliked ${cardTop?.text}")
        swipe()
      }

      OnSearchClick -> {
        searchRepository()
      }

      is OnSearchTextChange -> {
        _viewStateFlow.value = _viewStateFlow.value.copy(inputText = event.text)
      }
    }
  }

  private fun swipe() {
    currentIndex += 1
    _viewStateFlow.value = _viewStateFlow.value.copy(
      cardTop = items[currentIndex % items.size],
      cardBottom = items[(currentIndex + 1) % items.size],
    )
  }

  private fun searchRepository() {
    val searchText = viewStateFlow.value.inputText
    if (searchText.isBlank()) {
      return
    }
    loadingJob = viewModelScope.launch {
      val currentPage = 1
      _viewStateFlow.value = _viewStateFlow.value.copy(
        isLoading = true
      )
      when (val result = searchRepositoryUseCase.execute(searchText, currentPage)) {
        is Error -> {
          _viewStateFlow.value = _viewStateFlow.value.copy(
            isLoading = false,
            errorText = "Something wrong"
          )
        }

        is Success -> {
          val uiModels = result.item.map {
            CardUiModel(it.id)
          }
          items.clear()
          items.addAll(uiModels)
          _viewStateFlow.value = _viewStateFlow.value.copy(
            isLoading = false,
            currentPage = currentPage,
            cardTop = items.firstOrNull(),
            cardBottom = items.secondOrNull(),
          )
        }
      }
    }
  }

  private fun loadNewPage() {
    loadingJob = viewModelScope.launch {
      val searchText = viewStateFlow.value.inputText
      val res = searchRepositoryUseCase.execute(searchText, 1)
      Log.d("MainViewModel", res.toString())
    }
  }

  override fun onCleared() {
    super.onCleared()
    loadingJob?.cancel()
    loadingJob = null
  }

  private fun <T> List<T>.secondOrNull(): T? {
    return if (size < 2) null else this[1]
  }

}