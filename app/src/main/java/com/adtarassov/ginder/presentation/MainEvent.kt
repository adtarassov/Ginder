package com.adtarassov.ginder.presentation

sealed interface MainEvent {
  class OnSearchTextChange(val text: String) : MainEvent

  object OnRefreshClick : MainEvent

  object OnSwipeRight : MainEvent

  object OnSwipeLeft : MainEvent

  object OnSearchClick : MainEvent
}