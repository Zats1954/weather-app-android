package sk.solver.weatherapp.models

data class Sys (
    val type:Int = 0,
    val id: Int = 0,
    val message: Double = 0.0,
    val country: String? = null,
    val sunrise: Int = 0,
    val sunset: Int = 0
)