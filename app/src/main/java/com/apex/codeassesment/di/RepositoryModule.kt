package com.apex.codeassesment.di

import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.UserRepositoryImpl
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(localDataSource: LocalDataSource, remoteDataSource: RemoteDataSource): UserRepository {
        return UserRepositoryImpl(localDataSource = localDataSource, remoteDataSource = remoteDataSource)
    }
}