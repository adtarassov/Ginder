package com.adtarassov.ginder.presentation

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.adtarassov.ginder.R
import com.adtarassov.ginder.databinding.ActivityMainBinding
import com.adtarassov.ginder.presentation.CardUiModelState.Empty
import com.adtarassov.ginder.presentation.CardUiModelState.Error
import com.adtarassov.ginder.presentation.CardUiModelState.Loading
import com.adtarassov.ginder.presentation.MainEvent.OnRefreshClick
import com.adtarassov.ginder.presentation.MainEvent.OnSearchClick
import com.adtarassov.ginder.presentation.MainEvent.OnSearchTextChange
import com.adtarassov.ginder.presentation.MainEvent.OnSwipeLeft
import com.adtarassov.ginder.presentation.MainEvent.OnSwipeRight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private var _binding: ActivityMainBinding? = null
  private val binding: ActivityMainBinding
    get() = checkNotNull(_binding)

  private val viewModel: MainViewModel by viewModels()
  private val motionTransitionAdapter = MotionTransitionAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupSearchView()
    binding.searchButton.setOnClickListener { sendOnSearchEvent() }
    binding.likeButton.setOnClickListener {
      binding.root.transitionToState(R.id.like)
    }
    binding.dislikeButton.setOnClickListener {
      binding.root.transitionToState(R.id.dislike)
    }
    binding.root.setTransitionListener(motionTransitionAdapter)
    lifecycleScope.launch {
      viewModel.viewStateFlow.collectLatest(::bindState)
    }
  }

  override fun onStop() {
    binding.root.removeTransitionListener(motionTransitionAdapter)
    super.onStop()
  }

  private fun setupSearchView() {

    binding.searchView.setOnEditorActionListener { _, _, _ ->
      sendOnSearchEvent()
      false
    }

    binding.searchView.setOnClickListener {
      binding.searchView.isCursorVisible = true
    }


    binding.searchView.addTextChangedListener(
      onTextChanged = { text, _, _, _ ->
        val text = text.toString()
        viewModel.obtainEvent(OnSearchTextChange(text))
      }
    )
  }

  private fun sendOnSearchEvent() {
    hideKeyboard()
    viewModel.obtainEvent(OnSearchClick)
  }

  private fun onRefreshClick() {
    viewModel.obtainEvent(OnRefreshClick)
  }

  private fun bindState(state: MainState) {
    changeTransitionAvailable(disable = state.cardTop is Empty || state.cardTop is Loading || state.cardTop is Error)
    binding.cardOne.setState(state.cardTop, ::onRefreshClick)
    binding.cardTwo.setState(state.cardBottom, ::onRefreshClick)
  }

  private fun hideKeyboard() {
    binding.searchView.isCursorVisible = false
    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(binding.searchView.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
  }

  private fun changeTransitionAvailable(disable: Boolean) {
    binding.root.enableTransition(R.id.startToRight, !disable)
    binding.root.enableTransition(R.id.startToLeft, !disable)
    binding.dislikeButton.isEnabled = !disable
    binding.likeButton.isEnabled = !disable
  }

  private inner class MotionTransitionAdapter : TransitionAdapter() {

    override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
      if (startId == R.id.start) {
        val shouldEnableButtons = progress < 0.1f
        binding.dislikeButton.isEnabled = shouldEnableButtons
        binding.likeButton.isEnabled = shouldEnableButtons
      }
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
      when (currentId) {
        R.id.offScreenUnlike -> {
          viewModel.obtainEvent(OnSwipeLeft)
          binding.root.setTransition(R.id.startToRight)
          binding.root.progress = 0f
        }

        R.id.offScreenLike -> {
          viewModel.obtainEvent(OnSwipeRight)
          binding.root.setTransition(R.id.startToLeft)
          binding.root.progress = 0f
        }
      }
    }
  }
}