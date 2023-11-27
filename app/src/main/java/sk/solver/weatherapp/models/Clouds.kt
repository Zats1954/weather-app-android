package sk.solver.weatherapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clouds (
    var all: Int = 0
) : Parcelable