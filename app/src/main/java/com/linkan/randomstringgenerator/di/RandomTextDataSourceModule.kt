package com.linkan.randomstringgenerator.di

import com.linkan.randomstringgenerator.data.RandomTextProviderDataSource
import com.linkan.randomstringgenerator.domain.repository.RandomTextRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RandomTextDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindRandomTextRepository(randomTextProviderDataSource: RandomTextProviderDataSource) : RandomTextRepository
}