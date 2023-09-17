package com.adtarassov.ginder.presentation

sealed interface MainViewEvent {
  class OnSearchTextChange(val text: String) : MainViewEvent

  object OnRefreshClick : MainViewEvent

  object OnSwipeRight : MainViewEvent

  object OnSwipeLeft : MainViewEvent

  object OnSearchClick : MainViewEvent
}