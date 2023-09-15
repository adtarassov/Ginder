package com.adtarassov.ginder.data

import com.adtarassov.ginder.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
  private val gitApi: GitApi,
) : Repository {

  override suspend fun searchRepos(query: String): String {
    return gitApi.getData(query).body().toString()
  }
}