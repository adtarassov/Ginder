package com.adtarassov.ginder.presentation

data class MainState(
  val inputText: String,
  val currentPage: Int,
  val cardTop: CardUiModelState,
  val cardBottom: CardUiModelState
)