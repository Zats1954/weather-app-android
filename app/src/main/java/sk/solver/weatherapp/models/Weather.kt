package sk.solver.weatherapp.models

data class Weather (
    var id: Int = 0,
    var main: String?=null,
    var description: String? =null,
    var icon: String? =null
)