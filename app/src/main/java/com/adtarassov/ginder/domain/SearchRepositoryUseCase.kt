package com.adtarassov.ginder.domain

import com.adtarassov.ginder.data.RepositoryResponseModel
import com.adtarassov.ginder.data.ResponseState
import com.adtarassov.ginder.data.SearchResponseModel
import javax.inject.Inject

class SearchRepositoryUseCase @Inject constructor(
  private val repository: Repository,
) {

  suspend fun execute(query: String, page: Int): ResponseState<SearchResponseModel> {
    return repository.searchRepositories(query, page)
  }
}