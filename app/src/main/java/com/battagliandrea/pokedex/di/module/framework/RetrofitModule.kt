package com.battagliandrea.pokedex.di.module.framework

import android.content.Context
import com.battagliandrea.pokedex.BuildConfig
import com.battagliandrea.pokedex.datasource.PokeApiContract
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
open class RetrofitModule {


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //          OKHTTP
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //          RETROFIT
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient) =
            createRetrofit(gson, okHttpClient, BuildConfig.apiBaseUrl)


    @Provides
    @Singleton
    open fun provideApi(retrofit: Retrofit, context: Context): PokeApiContract = retrofit.create(PokeApiContract::class.java)


    private fun createRetrofit(gson: Gson,
                               okHttpClient: OkHttpClient,
                               endpoint: String): Retrofit {

        return Retrofit.Builder()
                .baseUrl(endpoint)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }
}
