package com.adtarassov.ginder.domain

import com.adtarassov.ginder.data.RepositoryResponseModel
import com.adtarassov.ginder.data.ResponseState

interface Repository {

  suspend fun searchRepositories(query: String, page: Int): ResponseState<List<RepositoryResponseModel>>

}