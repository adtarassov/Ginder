package com.adtarassov.ginder.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adtarassov.ginder.data.RepositoryResponseModel
import com.adtarassov.ginder.data.ResponseState.Error
import com.adtarassov.ginder.data.ResponseState.Success
import com.adtarassov.ginder.domain.SearchRepositoryUseCase
import com.adtarassov.ginder.presentation.MainEvent.OnRefreshClick
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
  private val searchRepositoryUseCase: SearchRepositoryUseCase,
) : ViewModel() {

  private var items = mutableListOf<CardUiModelState>()

  private val _viewStateFlow = MutableStateFlow(getInitialState())
  val viewStateFlow = _viewStateFlow.asStateFlow()

  private var loadingJob: Job? = null
  private var currentIndex = 0
  private var totalCount = 0
  private var lastQueryText: String = ""

  fun obtainEvent(event: MainEvent) {
    when (event) {

      OnSwipeRight -> {
        val cardTop = viewStateFlow.value.cardTop as? CardUiModelState.Success ?: return
        Log.d("MainViewModel", "You liked ${cardTop.id}")
        changeTopCard()
      }

      OnSwipeLeft -> {
        val cardTop = viewStateFlow.value.cardTop as? CardUiModelState.Success ?: return
        Log.d("MainViewModel", "You disliked ${cardTop.id}")
        changeTopCard()
      }

      OnRefreshClick -> {
        if (canLoadMore()) {
          getNewPage(lastQueryText)
        } else {
          searchRepository(lastQueryText)
        }
      }

      OnSearchClick -> {
        searchRepository(viewStateFlow.value.inputText)
      }

      is OnSearchTextChange -> {
        _viewStateFlow.value = _viewStateFlow.value.copy(inputText = event.text)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    loadingJob?.cancel()
    loadingJob = null
  }

  private fun changeTopCard() {
    currentIndex += 1
    if (currentIndex == items.size - 2 && canLoadMore()) {
      getNewPage(lastQueryText)
    }
    _viewStateFlow.value = _viewStateFlow.value.copy(
      cardTop = getTopCard(),
      cardBottom = getBottomCard(),
    )
  }

  private fun searchRepository(query: String) {
    if (query.isBlank()) {
      return
    }
    lastQueryText = query
    loadingJob = viewModelScope.launch {
      val currentPage = 1
      currentIndex = 0
      _viewStateFlow.value = _viewStateFlow.value.copy(
        cardTop = CardUiModelState.Loading
      )
      when (val result = searchRepositoryUseCase.execute(query, currentPage)) {
        is Error -> {
          _viewStateFlow.value = _viewStateFlow.value.copy(
            cardTop = CardUiModelState.Error("Something wrong, please try again")
          )
        }

        is Success -> {
          val uiModels = result.item.items.map { it.toUiModel() }
          totalCount = result.item.totalCount
          items.clear()
          items.addAll(uiModels)
          if (canLoadMore()) {
            items.add(CardUiModelState.Loading)
          }
          if (uiModels.isEmpty()) {
            _viewStateFlow.value = _viewStateFlow.value.copy(
              currentPage = currentPage,
              cardTop = CardUiModelState.Empty("Empty after search"),
              cardBottom = CardUiModelState.Empty("Empty after search"),
            )
          } else {
            _viewStateFlow.value = _viewStateFlow.value.copy(
              currentPage = currentPage,
              cardTop = getTopCard(),
              cardBottom = getBottomCard(),
            )
          }

        }
      }
    }
  }

  private fun getNewPage(query: String) {
    _viewStateFlow.value = _viewStateFlow.value.copy(
      cardTop = CardUiModelState.Loading
    )
    loadingJob = viewModelScope.launch {
      val currentPage = viewStateFlow.value.currentPage + 1
      when (val result = searchRepositoryUseCase.execute(query, currentPage)) {
        is Error -> {
          _viewStateFlow.value = _viewStateFlow.value.copy(
            cardTop = CardUiModelState.Error("Something wrong, please try again")
          )
        }

        is Success -> {
          val uiModels = result.item.items.map { it.toUiModel() }
          totalCount = result.item.totalCount
          items = items.filterIsInstance<CardUiModelState.Success>().toMutableList()
          items.addAll(uiModels)
          if (canLoadMore()) {
            items.add(CardUiModelState.Loading)
          }
          _viewStateFlow.value = _viewStateFlow.value.copy(
            currentPage = currentPage,
            cardTop = getTopCard(),
            cardBottom = getBottomCard(),
          )
        }
      }
    }
  }

  private fun getInitialState() = MainState(
    inputText = "",
    currentPage = 1,
    cardTop = CardUiModelState.Empty("Your list are empty"),
    cardBottom = CardUiModelState.Empty("Your list are empty"),
  )

  private fun getTopCard(): CardUiModelState {
    return items[currentIndex % items.size]

  }

  private fun getBottomCard(): CardUiModelState {
    return items[(currentIndex + 1) % items.size]
  }

  private fun RepositoryResponseModel.toUiModel(): CardUiModelState.Success =
    CardUiModelState.Success(
      id = id,
      repoName = name,
      userName = owner.login,
      avatarUrl = owner.avatarUrl
    )


  private fun canLoadMore() = totalCount > items.size

}