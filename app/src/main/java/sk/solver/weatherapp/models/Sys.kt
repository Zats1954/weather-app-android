package sk.solver.weatherapp.models

data class Sys (
    var type:Int = 0,
    var id: Int = 0,
    var message: Double = 0.0,
    var country: String? = null,
    var sunrise: Int = 0,
    var sunset: Int = 0
)