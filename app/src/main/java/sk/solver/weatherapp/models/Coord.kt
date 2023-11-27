package sk.solver.weatherapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coord(
    var lon: Double = 0.0,
    var lat: Double = 0.0
) : Parcelable