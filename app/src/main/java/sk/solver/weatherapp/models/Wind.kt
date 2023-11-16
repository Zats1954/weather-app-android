package sk.solver.weatherapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind (
    var deg: Int? = 0,
    var gust: Double? = 0.0,
    var speed: Double? = 0.0
) : Parcelable