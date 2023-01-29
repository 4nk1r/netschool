package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.data.network.AuthService
import io.fournkoner.netschool.data.network.JournalService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.data.utils.insertHeaders
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Const.HOST)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .cookieJar(
                        object : CookieJar {
                            private val cookies = mutableListOf<Cookie>()

                            override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
                                cookies.removeAll { it.expiresAt() < System.currentTimeMillis() }
                                return cookies.filter { it.matches(httpUrl) }
                            }

                            override fun saveFromResponse(httpUrl: HttpUrl, cookies: List<Cookie>) {
                                this.cookies.addAll(cookies)
                            }
                        }
                    )
                    .addNetworkInterceptor { chain ->
                        chain.proceed(chain.request().insertHeaders().debugValue()).debugValue()
                    }
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideJournalService(retrofit: Retrofit): JournalService {
        return retrofit.create()
    }
}