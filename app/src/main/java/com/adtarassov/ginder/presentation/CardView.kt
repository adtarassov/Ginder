package com.adtarassov.ginder.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.adtarassov.ginder.databinding.RepositoryCardViewBinding
import com.adtarassov.ginder.presentation.CardUiModelState.Empty
import com.adtarassov.ginder.presentation.CardUiModelState.Error
import com.adtarassov.ginder.presentation.CardUiModelState.Loading
import com.adtarassov.ginder.presentation.CardUiModelState.Success
import com.google.android.material.card.MaterialCardView

class CardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr) {

  private val binding = RepositoryCardViewBinding.inflate(LayoutInflater.from(context), this)

  fun setState(model: CardUiModelState, onRefresh: () -> Unit) {
    binding.progressBar.isVisible = model is Loading
    binding.refreshButton.isVisible = model is Error
    binding.refreshButton.setOnClickListener {
      onRefresh.invoke()
    }
    when (model) {
      is Empty -> {
        binding.informationText.text = model.text
      }

      is Error -> {
        binding.informationText.text = model.text
      }

      Loading -> {
        binding.informationText.text = ""
      }

      is Success -> {
        binding.informationText.text = model.repoName
      }
    }
  }

}