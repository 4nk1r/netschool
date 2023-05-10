package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.data.network.*
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.insertHeaders
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
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
                    .cookieJar(NetSchoolCookieJar)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor { chain ->
                        chain.proceed(chain.request().insertHeaders())
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

    @Provides
    @Singleton
    fun provideReportsService(retrofit: Retrofit): ReportsService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideMailService(retrofit: Retrofit): MailService {
        return retrofit.create()
    }
}
