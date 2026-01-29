package com.finance.app.di

import android.content.Context
import androidx.room.Room
import com.finance.app.BuildConfig
import com.finance.app.data.local.FinanceDatabase
import com.finance.app.data.local.PreferencesManager
import com.finance.app.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    // BASE_URL 从 BuildConfig 读取，在编译时配置
    // 配置方式：在 local.properties 中添加 BASE_URL=http://你的服务器地址/
    private val BASE_URL = BuildConfig.BASE_URL
    
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    fun provideOkHttpClient(preferencesManager: PreferencesManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 对于流式响应，使用 HEADERS 级别，避免读取整个响应体
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = runBlocking { preferencesManager.getTokenSync() }
            
            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            
            chain.proceed(newRequest)
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS) // 流式响应需要更长的读取超时
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    fun provideDatabase(context: Context): FinanceDatabase {
        return Room.databaseBuilder(
            context,
            FinanceDatabase::class.java,
            "finance_database"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    
    fun providePreferencesManager(context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}
