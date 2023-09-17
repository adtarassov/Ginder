package com.adtarassov.ginder.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import coil.load
import coil.request.Disposable
import coil.transform.CircleCropTransformation
import com.adtarassov.ginder.databinding.RepositoryCardViewBinding
import com.adtarassov.ginder.presentation.CardUiModelState.Empty
import com.adtarassov.ginder.presentation.CardUiModelState.Error
import com.adtarassov.ginder.presentation.CardUiModelState.Loading
import com.adtarassov.ginder.presentation.CardUiModelState.Success
import com.google.android.material.card.MaterialCardView

class RepositoryCardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : MaterialCardView(context, attrs, defStyleAttr) {

  private val binding = RepositoryCardViewBinding.inflate(LayoutInflater.from(context), this)

  private var disposable: Disposable? = null

  private fun clearPrevState() {
    binding.progressBar.isVisible = false
    binding.refreshButton.isVisible = false
    binding.informationText.isVisible = false
    binding.repoName.isVisible = false
    binding.userName.isVisible = false
    binding.forksCount.isVisible = false
    binding.watchersCount.isVisible = false
    binding.isArchive.isVisible = false
    binding.ownerImage.isVisible = false

    binding.informationText.text = ""
    binding.repoName.text = ""
    binding.userName.text = ""
    binding.forksCount.text = ""
    binding.watchersCount.text = ""
    binding.isArchive.text = ""
    binding.ownerImage.setImageDrawable(null)
    disposable?.dispose()
  }

  fun setState(model: CardUiModelState, onRefresh: () -> Unit) {
    clearPrevState()
    binding.refreshButton.setOnClickListener {
      onRefresh.invoke()
    }
    when (model) {
      is Empty -> {
        binding.informationText.isVisible = true
        binding.informationText.text = model.text
      }

      is Error -> {
        binding.refreshButton.isVisible = true
        binding.informationText.isVisible = true
        binding.informationText.text = model.text
      }

      Loading -> {
        binding.progressBar.isVisible = true
      }

      is Success -> {
        binding.repoName.isVisible = true
        binding.userName.isVisible = true
        binding.forksCount.isVisible = true
        binding.watchersCount.isVisible = true
        binding.isArchive.isVisible = true
        binding.ownerImage.isVisible = true

        binding.repoName.text = model.repoName
        binding.userName.text = model.userName

        binding.forksCount.text = model.forksCount
        binding.watchersCount.text = model.watchersCount
        binding.isArchive.text = model.isArchive

        disposable = binding.ownerImage.load(model.avatarUrl) {
          crossfade(true)
          transformations(CircleCropTransformation())
        }
      }
    }
  }

}