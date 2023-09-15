package com.adtarassov.ginder.domain

import javax.inject.Inject

class GetUseCase @Inject constructor(
  private val repository: Repository,
) {
  suspend fun searchRepos(query: String): String {
    return repository.searchRepos(query)
  }
}