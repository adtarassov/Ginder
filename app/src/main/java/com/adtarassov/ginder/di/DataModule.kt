package com.adtarassov.ginder.di

import com.adtarassov.ginder.data.RepositoryImpl
import com.adtarassov.ginder.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

  @Binds
  fun bindRepository(repository: RepositoryImpl): Repository

}