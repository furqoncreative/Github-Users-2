package com.furqoncreative.githubusers2.di

import com.furqoncreative.githubusers2.BuildConfig.API_TOKEN
import com.furqoncreative.githubusers2.BuildConfig.BASE_URL
import com.furqoncreative.githubusers2.data.remote.AppService
import com.furqoncreative.githubusers2.data.remote.RemoteDataSource
import com.furqoncreative.githubusers2.data.repository.AppRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false)

        httpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", "token $API_TOKEN")
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .addHeader("Connection", "close")
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return httpClientBuilder.build()

    }

    @Provides
    fun provideCharacterService(retrofit: Retrofit): AppService =
        retrofit.create(AppService::class.java)

    @Singleton
    @Provides
    fun provideCharacterRemoteDataSource(appService: AppService) = RemoteDataSource(appService)


    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: RemoteDataSource) =
        AppRepository(remoteDataSource)
}