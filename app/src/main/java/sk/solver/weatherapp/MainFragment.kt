package sk.solver.weatherapp


import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils.split
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import sk.solver.weatherapp.databinding.FragmentMainBinding
import sk.solver.weatherapp.models.WeatherResponse
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var recyclerView: RecyclerView
    private var resultList: MutableList<WeatherResponse> = mutableListOf()

    @OptIn(ExperimentalTime::class)
    private var timeSource = TimeSource.Monotonic

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//
//        outState.putParseable("state", ListWeather::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        binding = FragmentMainBinding.inflate(layoutInflater)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(RecyclerViewItemDecoration(context, R.drawable.divider))
        recyclerView.adapter = CitiesAdapter(
            object : OnInteractionListener {
                override fun showCity(weatherResponse: WeatherResponse) {
                    val bundle = Bundle()
                    bundle.putParcelable("weatherResponse", weatherResponse)
                    findNavController().navigate(R.id.action_placeholder_to_cityFrame, bundle)
                }
            },
            resultList,
            requireContext()
        )

        binding.cityEditor.setOnKeyListener({ _, keyCode: Int, par ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && par.action == KeyEvent.ACTION_UP) {
//                val elapsedTime = measureTimeMillis {
                    val citiesList = loadCities()
                    resultList.clear()
                    citiesList.forEachIndexed { ind, city ->
                        /** ind for test! **/
                        CoroutineScope(Dispatchers.IO).launch {
                            resultList.add(loadCity(city, ind).await())
                            withContext(Dispatchers.Main) {
                                recyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
//                }
//                System.out.println("********************* measureTime $elapsedTime")
            }
            false
        })
        return binding.root
    }

    private fun loadCities(): List<String> {
        return split(binding.cityEditor.text.toString(), ",").asList()
    }

    @OptIn(ExperimentalTime::class)
    private fun loadCity(city: String, ind: Int): LiveData<WeatherResponse> {
        /** ind for test! **/
        /** **/
//        val del = if (ind == 0) 2000L else 0L
        val resultLivedata = MutableLiveData<WeatherResponse>()
        WeatherClientBuilder.create(WeatherClient::class.java)
            .getWeather(
                city,
                "metric",
                WeatherClientBuilder.WEATHER_APP_ID
            )
            /** for test! **/
            /** **/
//            .delay(del, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ weatherResponse: WeatherResponse ->
                Toast.makeText(context, "Finished $city", Toast.LENGTH_LONG).show()
                resultLivedata.value = weatherResponse
//                System.out.println("********************** $city ${timeSource.markNow()}")
            }, { throwable: Throwable ->
                val message =
                    if (throwable.message!!.contains("HTTP 404", true))
                        "Unknown city $city"
                    else
                        "Unexpected error: " + throwable.message
                Toast.makeText(
                    context,
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

private fun Parcelable.putParcelable(s: String, resultList: MutableList<WeatherResponse>) {}
