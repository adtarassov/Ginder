package com.adtarassov.ginder.presentation

import com.adtarassov.ginder.presentation.MainViewState.Transition.Unknown

data class MainViewState(
  val cardTop: CardUiModelState,
  val cardBottom: CardUiModelState,
  val transitionType: Transition = Unknown
) {
  enum class Transition {
    Left, Right, Unknown
  }
}