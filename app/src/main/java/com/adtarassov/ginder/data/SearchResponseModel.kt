package com.adtarassov.ginder.data

import android.text.BoringLayout
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

  @SerializedName("watchers")
  val watchers: Long,

  @SerializedName("forks")
  val forks: Long,

  @SerializedName("archived")
  val archived: Boolean,
)

data class OwnerRepositoryResponseModel(
  @SerializedName("id")
  val id: Long,

  @SerializedName("login")
  val login: String,

  @SerializedName("avatar_url")
  val avatarUrl: String,
)