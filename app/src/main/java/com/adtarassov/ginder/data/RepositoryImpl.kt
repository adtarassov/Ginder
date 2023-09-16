package com.adtarassov.ginder.data

import com.adtarassov.ginder.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

private const val PER_PAGE = 20

@Singleton
class RepositoryImpl @Inject constructor(
  private val gitSearchService: GitSearchService,
) : Repository {

  override suspend fun searchRepositories(query: String, page: Int): ResponseState<List<RepositoryResponseModel>> {
    return try {
      val response = gitSearchService.searchRepositories(
        query = query,
        page = page,
        perPage = PER_PAGE
      ).body()
      val items = response?.items ?: emptyList()
      ResponseState.Success(items)
    } catch (e: Exception) {
      ResponseState.Error(e)
    }
  }

}