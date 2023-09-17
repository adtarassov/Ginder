package com.adtarassov.ginder.presentation

import androidx.annotation.StringRes

sealed interface CardUiModelState {
  data class Empty(
    @StringRes
    val textId: Int? = null,
  ) : CardUiModelState

  data class Success(
    val id: Long,
    val repoName: String,
    val userName: String,
    val avatarUrl: String,
    val forksCount: String,
    val watchersCount: String,
    val isArchive: Boolean,
  ) : CardUiModelState

  object Loading : CardUiModelState

  data class Error(
    @StringRes
    val textId: Int? = null,
  ) : CardUiModelState
}