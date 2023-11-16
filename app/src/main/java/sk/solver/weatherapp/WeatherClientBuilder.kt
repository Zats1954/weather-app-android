package sk.solver.weatherapp

import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.annotation.JsonAutoDetect
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object WeatherClientBuilder {
    private const val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/"
    const val WEATHER_APP_ID = "f00c38e0279b7bc85480c3fe775d518c"
    private var instance: Retrofit? = null
    private val builderInstance: Retrofit?
        get() {
            if (instance != null) {
                return instance
            }
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()
            val objectMapper = ObjectMapper()
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            instance = Retrofit.Builder()
                .baseUrl(WEATHER_API_URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
            return instance
        }

    fun <T> create(serviceInterface: Class<T>?): T {
        return builderInstance!!.create(serviceInterface)
    }
}