package sk.solver.weatherapp

import sk.solver.weatherapp.models.WeatherResponse
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListWeather(
    val listWeather: List<WeatherResponse>
) : Parcelable
