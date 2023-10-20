package sk.solver.weatherapp.models

data class Weather (
    val id: Int = 0,
    val main: String?=null,
    val description: String? =null,
    val icon: String? =null
)