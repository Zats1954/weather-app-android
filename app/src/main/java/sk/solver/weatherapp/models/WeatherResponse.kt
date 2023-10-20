package sk.solver.weatherapp.models

data class WeatherResponse(
    val coord: Coord?  =null,
    val weather: List<Weather> = emptyList(),
    val base: String?  =null,
    val main: Main? = null,
    val visibility: Int = 0,
    val wind: Wind?  =null,
    val clouds: Clouds?  =null,
    val dt: Int = 0,
    val sys: Sys?  =null,
    val timezone: Int = 0,
    val id: Int = 0,
    val name: String?  =null,
    val cod: Int = 0
)