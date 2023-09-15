package com.adtarassov.ginder.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitApi {

  @GET("/search/repositories")
  suspend fun getData(
    @Query("q")
    query: String,
  ): Response<Any>

}