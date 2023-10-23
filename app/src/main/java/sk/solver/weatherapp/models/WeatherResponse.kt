package sk.solver.weatherapp.models

data class WeatherResponse(
    var coord: Coord?  =null,
    var weather: List<Weather> = emptyList(),
    var base: String?  =null,
    var main: Main? = null,
    var visibility: Int = 0,
    var wind: Wind?  =null,
    var clouds: Clouds?  =null,
    var dt: Int = 0,
    var sys: Sys?  =null,
    var timezone: Int = 0,
    var id: Int = 0,
    var name: String?  =null,
    var cod: Int = 0
)