package fastcampus.aos.part2.part2_chapter8

import android.content.res.Resources
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object SearchRepository {

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(AppInterceptor()).build()

    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openapi.naver.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    private val service = retrofit.create(SearchService::class.java)

    fun getGoodRestaurant(query: String): Call<SearchResult> = service.getGoodRestaurant(query = "$query 맛집", display = 30)

    class AppInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val clientId = MyApplication.applicationContext.getString(R.string.naver_client_id)
            val clientSecret = MyApplication.applicationContext.getString(R.string.naver_client_secret)

            val newRequest = chain.request().newBuilder()
                .addHeader("X-Naver-Client-Id", clientId)
                .addHeader("X-Naver-Client-Secret", clientSecret)
                .build()

            return chain.proceed(newRequest)
        }
    }
}