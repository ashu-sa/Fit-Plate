package com.example.fitplate.di

import com.example.fitplate.BuildConfig
import com.example.fitplate.data.network.FitPlateNetworkApi
import com.example.fitplate.data.network.GroqApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideFitPlateNetworkApi(builder: Retrofit.Builder): FitPlateNetworkApi = builder
        .baseUrl(BuildConfig.SPOONACULAR_URL)
        .build()
        .create(FitPlateNetworkApi::class.java)

    @Provides
    @Singleton
    fun provideGroqApi(builder: Retrofit.Builder): GroqApi = builder
        .baseUrl("https://api.groq.com/")
        .build()
        .create(GroqApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofitBuilder(client: OkHttpClient): Retrofit.Builder = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
        )
        .build()
}
