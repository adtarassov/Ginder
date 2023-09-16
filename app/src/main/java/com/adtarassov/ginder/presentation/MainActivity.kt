package com.adtarassov.ginder.presentation

import android.opengl.Visibility
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.adtarassov.ginder.R
import com.adtarassov.ginder.databinding.ActivityMainBinding
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

  private fun bindState(state: MainState) {
    changeTransitionAvailable(state.isLoading || state.cardTop == null || state.cardBottom == null)
    state.cardTop?.let { binding.cardOne.setState(it) }
    state.cardBottom?.let { binding.cardTwo.setState(it) }
    binding.progressBar.visibility = if (state.isLoading) VISIBLE else INVISIBLE
    binding.root.progress = 0f
    binding.root.setTransition(R.id.startToLeft)
  }

  private fun hideKeyboard() {
    binding.searchView.isCursorVisible = false
    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(binding.searchView.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
  }

  private fun changeTransitionAvailable(disable: Boolean) {
    binding.root.enableTransition(R.id.startToRight, !disable)
    binding.root.enableTransition(R.id.startToLeft, !disable)
  }

  private inner class MotionTransitionAdapter : TransitionAdapter() {
    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
      when (currentId) {
        R.id.offScreenUnlike -> {
          viewModel.obtainEvent(OnSwipeLeft)
        }

        R.id.offScreenLike -> {
          viewModel.obtainEvent(OnSwipeRight)
        }
      }
    }
  }
}