package com.adtarassov.ginder.data

import com.google.gson.annotations.SerializedName

data class SearchResponseModel(
  @SerializedName("total_count")
  val totalCount: Int,

  @SerializedName("incomplete_results")
  val incompleteResults: Boolean,

  @SerializedName("items")
  val items: List<RepositoryResponseModel>,
)

data class RepositoryResponseModel(
  val id: String,
)