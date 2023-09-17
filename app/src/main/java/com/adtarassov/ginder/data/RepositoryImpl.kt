package com.adtarassov.ginder.data

import com.adtarassov.ginder.domain.Repository
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

private const val PER_PAGE = 20

@Singleton
class RepositoryImpl @Inject constructor(
  private val gitSearchService: GitSearchService,
) : Repository {

  override suspend fun searchRepositories(query: String, page: Int): ResponseState<SearchResponseModel> {
    return try {
      val response = gitSearchService.searchRepositories(
        query = query,
        page = page,
        perPage = PER_PAGE
      ).body()
      if (response == null) {
        ResponseState.Error(IllegalStateException("sada"))
      } else {
        ResponseState.Success(response)
      }
    } catch (e: Exception) {
      ResponseState.Error(e)
    }
  }

}