package com.example.yelpclone.di

import com.example.yelpclone.BuildConfig
import com.example.yelpclone.data.business.repository.DefaultBusinessRepository
import com.example.yelpclone.data.business.source.BusinessApi
import com.example.yelpclone.domain.business.repository.BusinessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Constructs shared HTTP dependencies and binds feature repository contracts for Hilt.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Named("yelpKey")
    fun yelpKey(): String = BuildConfig.YELP_API_KEY

    @Provides
    @Singleton
    fun httpClient(): OkHttpClient {
        val logging =
            HttpLoggingInterceptor().apply {
                redactHeader("Authorization")
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            }
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Provides
    @Singleton
    fun businessApi(client: OkHttpClient): BusinessApi =
        retrofit("https://api.yelp.com/v3/", client).create(BusinessApi::class.java)

    @Provides fun businessRepository(repository: DefaultBusinessRepository): BusinessRepository = repository

    private fun retrofit(
        url: String,
        client: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
