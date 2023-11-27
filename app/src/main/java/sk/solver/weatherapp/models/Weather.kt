package sk.solver.weatherapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather (
    var id: Int = 0,
    var main: String?=null,
    var description: String? =null,
    var icon: String? =null
) : Parcelable