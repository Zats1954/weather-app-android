package sk.solver.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import sk.solver.weatherapp.databinding.FragmentCityBinding
import sk.solver.weatherapp.models.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

class CityFragment : Fragment() {

    private var weather: WeatherResponse? = null
    private val formatter = SimpleDateFormat(" HH:mm:ss")
    private var timezoneOffset = 0
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weather = it.getParcelable("weatherResponse")
        }
        timezoneOffset = calendar.timeZone.rawOffset
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCityBinding.inflate(inflater, container, false)
        arguments?.getParcelable<WeatherResponse>("weatherResponse")?.let { weather ->

            val str = weather.weather[0].icon
            val iconString = getImageId(str)
            Glide.with(binding.imageView).load(iconString)
                .override(100, 100)
                .circleCrop()
                .placeholder(R.drawable.ic_o_24dp)
                .error(R.drawable.ic_o_24dp)
                .into(binding.imageView)

            val currentTime = formatter
                .format((weather.dt + weather.timezone) * 1000L - timezoneOffset)

            val windSector = weather.wind?.deg?.div(22.5f)?.toInt()
            val windDirection = when (windSector) {
                0, 15 -> "N  "
                1, 2 -> "NE  "
                3, 4 -> "E  "
                5, 6 -> "SE  "
                7, 8 -> "S  "
                9, 10 -> "SW  "
                11, 12 -> "W  "
                13, 14 -> "NW  "
                else -> "??"
            }

            binding.city.text = weather.name
            binding.localTime.text = currentTime
            binding.cloud.text = weather.weather[0].description
            binding.wind.text = windDirection
            binding.windSpeed.text = weather.wind?.speed.toString().plus(" m/s")
            binding.temp.text = weather.main?.temp.toString() + " °C"
            binding.pressure.text = weather.main?.pressure.toString()
            binding.humidity.text = weather.main?.humidity.toString() + "%"
            binding.button.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

    private fun getImageId(str: String?): Int {
        return when (str) {
            "01d" -> R.drawable.ic__01d
            "01n" -> R.drawable.ic__01n
            "02d" -> R.drawable.ic__02d
            "02n" -> R.drawable.ic__02n
            "03d" -> R.drawable.ic__03d
            "03n" -> R.drawable.ic__03n
            "04d" -> R.drawable.ic__04d
            "04n" -> R.drawable.ic__04n
            "09d" -> R.drawable.ic__09d
            "09n" -> R.drawable.ic__09n
            "10d" -> R.drawable.ic__10d
            "10n" -> R.drawable.ic__10n
            "11d" -> R.drawable.ic__11d
            "11n" -> R.drawable.ic__11n
            "13d" -> R.drawable.ic__13d
            "13n" -> R.drawable.ic__13n
            "50d" -> R.drawable.ic__50d
            "50n" -> R.drawable.ic__50n
            else -> R.drawable.ic_o_24dp
        }
    }
}