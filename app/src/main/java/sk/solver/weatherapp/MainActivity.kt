package sk.solver.weatherapp


import android.os.Bundle
import android.text.TextUtils.split
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import sk.solver.weatherapp.databinding.ActivityMainBinding
import sk.solver.weatherapp.models.WeatherResponse
import java.util.concurrent.TimeUnit

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
                    citiesList.forEachIndexed { ind, city ->
                        /** ind for test! **/
                        launch {
                            loadCity(city, ind).await()
                            withContext(Dispatchers.Main) {
                                recyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
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

    private fun loadCity(city: String, ind: Int): LiveData<Boolean> {
        /** ind for test! **/
        val del = if (ind == 0) 2000L else 0L
        val resultLivedata = MutableLiveData<Boolean>()
        WeatherClientBuilder.create(WeatherClient::class.java)
            .getWeather(
                city,
                "metric",
                WeatherClientBuilder.WEATHER_APP_ID
            )
            /** for test! **/
            .delay(del, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weatherResponse: WeatherResponse ->
                Toast.makeText(this, "Finished $city", Toast.LENGTH_LONG).show()
                val objectMapper = ObjectMapper()
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                resultList.add(weatherResponse)
                resultLivedata.value = true
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
        return resultLivedata
    }

    suspend fun <T> LiveData<T>.await(): T {
        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                val observer = object : Observer<T> {
                    override fun onChanged(value: T) {
                        removeObserver(this)
                        continuation.resume(value, null)
                    }
                }
                observeForever(observer)
                continuation.invokeOnCancellation {
                    removeObserver(observer)
                }
            }
        }
    }


}