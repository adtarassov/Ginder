package com.adtarassov.ginder.presentation

data class MainState(
  val inputText: String,
  val currentPage: Int,
  val isLoading: Boolean,
  val errorText: String?,
  val cardTop: CardUiModel? = null,
  val cardBottom: CardUiModel? = null,
) {

  companion object {
    val EMPTY = MainState(
      inputText = "",
      currentPage = 1,
      isLoading = false,
      errorText = null,
      cardTop = null,
      cardBottom = null,
    )
  }
}