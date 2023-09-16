package com.adtarassov.ginder.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitSearchService {

  @GET("/search/repositories")
  suspend fun searchRepositories(
    @Query("q") query: String,
    @Query("page") page: Int,
    @Query("per_page") perPage: Int,
  ): Response<SearchResponseModel>

}