package com.adtarassov.ginder.presentation

sealed interface CardUiModelState {
  data class Empty(val text: String) : CardUiModelState

  data class Success(
    val id: Long,
    val repoName: String,
    val userName: String,
    val avatarUrl: String,
  ) : CardUiModelState

  object Loading : CardUiModelState

  data class Error(val text: String) : CardUiModelState
}