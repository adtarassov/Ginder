package com.adtarassov.ginder.domain

interface Repository {

  suspend fun searchRepos(query: String): String

}