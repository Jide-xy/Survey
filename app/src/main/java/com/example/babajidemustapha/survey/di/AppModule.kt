package com.example.babajidemustapha.survey.di

import android.content.Context
import com.jide.surveyapp.di.RepositoryFactory
import com.jide.surveyapp.repository.Repository
import com.jide.surveyapp.repository.local.DatabaseDriverFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context): Repository {
        return RepositoryFactory.create(DatabaseDriverFactory(context))
    }
}