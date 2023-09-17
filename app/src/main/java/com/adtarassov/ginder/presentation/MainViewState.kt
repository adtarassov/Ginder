package com.adtarassov.ginder.presentation

data class MainViewState(
  val inputText: String,
  val currentPage: Int,
  val cardTop: CardUiModelState,
  val cardBottom: CardUiModelState
)