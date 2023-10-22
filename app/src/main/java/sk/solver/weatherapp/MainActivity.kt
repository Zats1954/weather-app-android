package sk.solver.weatherapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.split
import android.view.KeyEvent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import sk.solver.weatherapp.databinding.ActivityMainBinding
import sk.solver.weatherapp.models.WeatherResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var citiesList: List<String>
    private lateinit var resultList: List<WeatherResponse>
    var listWeather : MutableList<WeatherResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultList = emptyList()


        binding.cityEditor.setOnKeyListener { _, keyCode: Int, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                citiesList = loadCities()
                citiesList.forEach { city ->
                    listWeather.add(loadCity(city))
                    System.out.println("********************* resultList.size  ${resultList.size}")
                }
            }
            false
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AdapterClass(resultList)
    }

    private fun loadCities(): List<String> {
        return split(binding.cityEditor.text.toString(),"/").asList()
//        cities.forEachIndexed {id,str -> System.out.println("****** $id")
    }

    private fun loadCity(city:  String ):  WeatherResponse  {
        var returnWeather = WeatherResponse()
        WeatherClientBuilder.create(WeatherClient::class.java)
            .getWeather(
//                binding.cityEditor.text.toString(),
                 city,
                "metric",
                WeatherClientBuilder.WEATHER_APP_ID
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weatherResponse: WeatherResponse ->
                Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show()
                val objectMapper = ObjectMapper()
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
//                binding.output.text =
                objectMapper.writeValueAsString(weatherResponse)
//                binding.city.text = weatherResponse.name
//                binding.cloud.text = weatherResponse.weather.get(0).main?.toString()
//                binding.temp.text = weatherResponse.main?.temp.toString() +" °C"
//                binding.pressure.text = weatherResponse.main?.pressure.toString()
//                binding.humidity.text = weatherResponse.main?.humidity.toString() + " %"
//                returnWeather = weatherResponse
            }) { throwable: Throwable ->
                Toast.makeText(
                    this,
                    "Unexpected error: " + throwable.message,
                    Toast.LENGTH_LONG
                ).show()
                System.out.println(throwable.message)
            }
           return returnWeather

    }
}