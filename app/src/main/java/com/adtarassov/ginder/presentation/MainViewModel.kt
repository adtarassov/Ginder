package com.adtarassov.ginder.presentation

import android.util.Log
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adtarassov.ginder.R
import com.adtarassov.ginder.data.RepositoryResponseModel
import com.adtarassov.ginder.data.ResponseState.Error
import com.adtarassov.ginder.data.ResponseState.Success
import com.adtarassov.ginder.domain.SearchRepositoryUseCase
import com.adtarassov.ginder.presentation.MainViewEvent.OnRefreshClick
import com.adtarassov.ginder.presentation.MainViewEvent.OnSearchClick
import com.adtarassov.ginder.presentation.MainViewEvent.OnSearchTextChange
import com.adtarassov.ginder.presentation.MainViewEvent.OnSwipeLeft
import com.adtarassov.ginder.presentation.MainViewEvent.OnSwipeRight
import com.adtarassov.ginder.presentation.MainViewState.Transition.Left
import com.adtarassov.ginder.presentation.MainViewState.Transition.Right
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LAST_QUERY_TEXT_KEY = "LAST_QUERY_TEXT_KEY"

@HiltViewModel
class MainViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val searchRepositoryUseCase: SearchRepositoryUseCase,
) : ViewModel() {

  private var items = mutableListOf<CardUiModelState>()

  private val _viewStateFlow = MutableStateFlow(getInitialState())
  val viewStateFlow = _viewStateFlow.asStateFlow()

  private var loadingJob: Job? = null
  private var currentIndex = 0
  private var currentPage = 1
  private var totalCount = 0
  private var lastQueryText = ""
  private var queryText = ""

  init {
    val prevInputText = savedStateHandle[LAST_QUERY_TEXT_KEY] ?: ""
    if (prevInputText.isNotBlank()) {
      searchRepository(prevInputText)
    }
  }

  fun obtainEvent(event: MainViewEvent) {
    when (event) {

      is OnSwipeRight -> {
        val cardTop = viewStateFlow.value.cardTop as? CardUiModelState.Success ?: return
        Log.d("MainViewModel", "You liked ${cardTop.id}")
        changeTopCard(event)
      }

      is OnSwipeLeft -> {
        val cardTop = viewStateFlow.value.cardTop as? CardUiModelState.Success ?: return
        Log.d("MainViewModel", "You disliked ${cardTop.id}")
        changeTopCard(event)
      }

      OnRefreshClick -> {
        if (canLoadMore()) {
          getNewPage(lastQueryText)
        } else {
          searchRepository(lastQueryText)
        }
      }

      OnSearchClick -> {
        searchRepository(queryText)
      }

      is OnSearchTextChange -> {
        val text = event.text
        queryText = text
      }
    }
  }

  private fun changeTopCard(event: MainViewEvent) {
    currentIndex += 1
    if (currentIndex == items.size - 2 && canLoadMore()) {
      getNewPage(lastQueryText)
    }
    _viewStateFlow.value = _viewStateFlow.value.copy(
      cardTop = getTopCard(),
      cardBottom = getBottomCard(),
      transitionType = if (event is OnSwipeLeft) Left else Right
    )
  }

  private fun searchRepository(query: String) {
    if (query.isBlank()) {
      return
    }
    lastQueryText = query
    savedStateHandle[LAST_QUERY_TEXT_KEY] = query
    currentPage = 1
    currentIndex = 0
    loadingJob = viewModelScope.launch {
      _viewStateFlow.value = _viewStateFlow.value.copy(
        cardTop = CardUiModelState.Loading
      )
      when (val result = searchRepositoryUseCase.execute(query, currentPage)) {
        is Error -> {
          _viewStateFlow.value = _viewStateFlow.value.copy(
            cardTop = CardUiModelState.Error(R.string.error)
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
              cardTop = CardUiModelState.Empty(R.string.empty_list),
              cardBottom = CardUiModelState.Empty(),
            )
          } else {
            _viewStateFlow.value = _viewStateFlow.value.copy(
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
      val newCurrentPage = currentPage + 1
      when (val result = searchRepositoryUseCase.execute(query, newCurrentPage)) {
        is Error -> {
          _viewStateFlow.value = _viewStateFlow.value.copy(
            cardTop = CardUiModelState.Error(R.string.error)
          )
        }

        is Success -> {
          val uiModels = result.item.items.map { it.toUiModel() }
          totalCount = result.item.totalCount
          currentPage = newCurrentPage
          items = items.filterIsInstance<CardUiModelState.Success>().toMutableList()
          items.addAll(uiModels)
          if (canLoadMore()) {
            items.add(CardUiModelState.Loading)
          }
          _viewStateFlow.value = _viewStateFlow.value.copy(
            cardTop = getTopCard(),
            cardBottom = getBottomCard(),
          )
        }
      }
    }
  }

  private fun getInitialState() = MainViewState(
    cardTop = CardUiModelState.Empty(R.string.empty_list),
    cardBottom = CardUiModelState.Empty(),
  )

  private fun getTopCard() = items[currentIndex % items.size]

  private fun getBottomCard() = items[(currentIndex + 1) % items.size]

  private fun RepositoryResponseModel.toUiModel(): CardUiModelState.Success =
    CardUiModelState.Success(
      id = id,
      repoName = name,
      avatarUrl = owner.avatarUrl,
      userName = owner.login,
      forksCount = forks.toString(),
      watchersCount = watchers.toString(),
      isArchive = archived,
    )

  private fun canLoadMore() = totalCount > items.size

}