package sk.solver.weatherapp


import android.os.Bundle
import android.text.TextUtils.split
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import sk.solver.weatherapp.databinding.ActivityMainBinding
import sk.solver.weatherapp.models.WeatherResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private var resultList: MutableList<WeatherResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cityEditor.setOnKeyListener { par0, keyCode: Int, par ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && par.action == KeyEvent.ACTION_UP) {
                resultList = mutableListOf()
                recyclerView.adapter = CitiesAdapter(resultList, applicationContext)
                val citiesList = loadCities()
                lifecycleScope.launch {
                    citiesList.forEachIndexed {ind, city ->
 /** ind for test! **/
                        val valueDeferred = async { loadCity(city, ind) }
                        delay(1500)
                        valueDeferred.await()

                    }
                    withContext(Dispatchers.Main) {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
            false
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(RecyclerViewItemDecoration(this, R.drawable.divider))


    }

    private fun loadCities(): List<String> {
        return split(binding.cityEditor.text.toString(), "/").asList()
    }

    private fun loadCity(city: String, ind: Int)  {
/** ind for test! **/ val del = if(ind == 0) 1500L else 0L
         WeatherClientBuilder.create(WeatherClient::class.java)
            .getWeather(
                city,
                "metric",
                WeatherClientBuilder.WEATHER_APP_ID
            )
/** for test! **/ .delay( del ,TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weatherResponse: WeatherResponse ->
                Toast.makeText(this, "Finished $city", Toast.LENGTH_LONG).show()
                val objectMapper = ObjectMapper()
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                resultList.add(weatherResponse)
            }, { throwable: Throwable ->
                val message =
                    if (throwable.message!!.contains("HTTP 404", true))
                        "Unknown city $city"
                    else
                        "Unexpected error: " + throwable.message
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
                System.out.println(throwable.message)
            })
    }


}