package com.adtarassov.ginder.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.adtarassov.ginder.databinding.RepositoryCardViewBinding
import com.google.android.material.card.MaterialCardView

class CardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr) {

  private val binding = RepositoryCardViewBinding.inflate(LayoutInflater.from(context), this)

  fun setState(model: CardUiModel) {
    binding.text.text = model.text
  }

}