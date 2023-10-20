package sk.solver.weatherapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.concat
import android.text.TextUtils.split
import android.view.KeyEvent
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import sk.solver.weatherapp.databinding.ActivityMainBinding
import sk.solver.weatherapp.models.WeatherResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cityEditor.setOnKeyListener { _, keyCode: Int, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                loadCity()
            }
            false
        }
    }

    private fun loadCity() {
        val cities = split(binding.cityEditor.text.toString(),"/")
        cities.forEachIndexed {id,str -> System.out.println("****** $id")
        WeatherClientBuilder.create(WeatherClient::class.java)
            .getWeather(
//                binding.cityEditor.text.toString(),
                 str,
                "metric",
                WeatherClientBuilder.WEATHER_APP_ID
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weatherResponse: WeatherResponse ->
                Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show()
                val objectMapper = ObjectMapper()
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
//                binding.output.text =
                objectMapper.writeValueAsString(weatherResponse)
                binding.city.text = weatherResponse.name
                binding.cloud.text = weatherResponse.weather.get(0).main
                binding.temp.text = weatherResponse.main?.temp.toString() +" °C"
                binding.pressure.text = weatherResponse.main?.pressure.toString()
                binding.humidity.text = weatherResponse.main?.humidity.toString() + " %"
            }) { throwable: Throwable ->
                Toast.makeText(
                    this,
                    "Unexpected error: " + throwable.message,
                    Toast.LENGTH_LONG
                ).show()
                System.out.println(throwable.message)
            }
        }
    }
}