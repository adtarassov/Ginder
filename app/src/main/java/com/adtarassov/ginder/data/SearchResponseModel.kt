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
  @SerializedName("id")
  val id: Long,

  @SerializedName("name")
  val name: String,

  @SerializedName("owner")
  val owner: OwnerRepositoryResponseModel,
)

data class OwnerRepositoryResponseModel(
  @SerializedName("id")
  val id: Long,

  @SerializedName("login")
  val login: String,

  @SerializedName("avatar_url")
  val avatarUrl: String,
)