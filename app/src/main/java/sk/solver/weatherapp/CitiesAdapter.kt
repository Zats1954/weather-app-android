package sk.solver.weatherapp


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sk.solver.weatherapp.models.WeatherResponse


class CitiesAdapter(
    private val dataList: List<WeatherResponse>,
    private val context: Context
) : RecyclerView.Adapter<CitiesAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        val str = currentItem.weather[0].icon
        val iconString = getImageId(str)

        Glide.with(holder.rContext).load(iconString)
            .override(100, 100)
            .circleCrop()
            .placeholder(R.drawable.ic_o_24dp)
            .error(R.drawable.ic_o_24dp)
            .into(holder.rvIcon)

        holder.rvCity.text = currentItem.name
        holder.rvCloud.text = currentItem.weather[0].description
        holder.rvTemperature.text = currentItem.main?.temp.toString() + " °C"
        holder.rvPressure.text = currentItem.main?.pressure.toString()
        holder.rvHumidity.text = currentItem.main?.humidity.toString() + "%"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

 class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rContext = itemView.getContext()
        val rvCity: TextView = itemView.findViewById(R.id.city)
        val rvIcon: ImageView = itemView.findViewById(R.id.imageView)
        val rvCloud: TextView = itemView.findViewById(R.id.cloud)
        val rvTemperature: TextView = itemView.findViewById(R.id.temp)
        val rvPressure: TextView = itemView.findViewById(R.id.pressure)
        val rvHumidity: TextView = itemView.findViewById(R.id.humidity)
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

            else ->  R.drawable.ic_o_24dp
        }
    }
}