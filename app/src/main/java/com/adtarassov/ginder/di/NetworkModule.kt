package com.adtarassov.ginder.di

import com.adtarassov.ginder.data.GitSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.github.com"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

  @Provides
  @Singleton
  fun provideGitApi(retrofit: Retrofit): GitSearchService = retrofit.create(GitSearchService::class.java)

}